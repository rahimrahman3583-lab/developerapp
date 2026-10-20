import os
import json
import sqlite3
from flask import Flask, render_template, request, redirect, url_for, session, jsonify, flash
from database import get_db, init_db, hash_password

app = Flask(__name__)
app.secret_key = os.environ.get("SECRET_KEY", "quranify_dev_secret_key_84626")

# Initialize database on startup
init_db()

@app.before_request
def require_admin_auth():
    # Enforce role checking for /admin routes (except /admin/login)
    if request.path.startswith("/admin") and request.path != "/admin/login":
        if "user_id" not in session or session.get("role") != "admin":
            flash("প্রবেশের জন্য অ্যাডমিনিস্ট্রেটর হিসেবে লগইন করুন।", "danger")
            return redirect(url_for("admin_login"))

@app.route("/")
def index():
    return redirect(url_for("admin_dashboard"))

# ==================== ADMIN PANEL ROUTES ====================

@app.route("/admin/login", methods=["GET", "POST"])
def admin_login():
    if request.method == "POST":
        username = request.form.get("username", "").strip()
        password = request.form.get("password", "").strip()

        conn = get_db()
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM users WHERE username = ? LIMIT 1", (username,))
        user = cursor.fetchone()
        conn.close()

        if user and user["password_hash"] == hash_password(password):
            if user["role"] == "admin":
                session["user_id"] = user["id"]
                session["username"] = user["username"]
                session["role"] = user["role"]
                session["display_name"] = user["display_name"]
                flash(f"স্বাগতম, {user['display_name']}!", "success")
                return redirect(url_for("admin_dashboard"))
            else:
                flash("এই অ্যাকাউন্টে অ্যাডমিনিস্ট্রেটর অ্যাক্সেস নেই।", "danger")
        else:
            flash("ভুল ইউজারনেম অথবা পাসওয়ার্ড।", "danger")

    return render_template("login.html")

@app.route("/admin/logout")
def admin_logout():
    session.clear()
    flash("লগআউট সফল হয়েছে।", "info")
    return redirect(url_for("admin_login"))

@app.route("/admin")
@app.route("/admin/dashboard")
def admin_dashboard():
    conn = get_db()
    cursor = conn.cursor()

    cursor.execute("SELECT COUNT(*) as cnt FROM users")
    total_users = cursor.fetchone()["cnt"]

    cursor.execute("SELECT COUNT(*) as cnt FROM users WHERE is_premium = 1")
    premium_users = cursor.fetchone()["cnt"]

    cursor.execute("SELECT COUNT(*) as cnt FROM surahs")
    total_surahs = cursor.fetchone()["cnt"]

    cursor.execute("SELECT COUNT(*) as cnt FROM verses")
    total_verses = cursor.fetchone()["cnt"]

    cursor.execute("SELECT COUNT(*) as cnt FROM payment_requests WHERE status = 'Pending'")
    pending_payments = cursor.fetchone()["cnt"]

    cursor.execute("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 5")
    recent_logs = cursor.fetchall()

    conn.close()

    return render_template(
        "dashboard.html",
        total_users=total_users,
        premium_users=premium_users,
        total_surahs=total_surahs,
        total_verses=total_verses,
        pending_payments=pending_payments,
        recent_logs=recent_logs
    )

@app.route("/admin/users", methods=["GET", "POST"])
def admin_users():
    conn = get_db()
    cursor = conn.cursor()

    if request.method == "POST":
        action = request.form.get("action")
        target_user_id = request.form.get("user_id")

        if action == "toggle_premium":
            cursor.execute("SELECT is_premium, username FROM users WHERE id = ?", (target_user_id,))
            target = cursor.fetchone()
            if target:
                new_status = 0 if target["is_premium"] == 1 else 1
                plan = "Admin Lifetime" if new_status == 1 else "Free"
                cursor.execute("UPDATE users SET is_premium = ?, premium_plan = ? WHERE id = ?", (new_status, plan, target_user_id))
                cursor.execute("""
                INSERT INTO audit_logs (admin_username, action, target, details)
                VALUES (?, ?, ?, ?)
                """, (session.get("username", "Admin"), "Toggle Premium", target["username"], f"Status: {new_status}"))
                conn.commit()
                flash(f"ইউজার {target['username']} এর প্রিমিয়াম স্ট্যাটাস আপডেট হয়েছে।", "success")

    search = request.args.get("q", "").strip()
    if search:
        cursor.execute("SELECT * FROM users WHERE username LIKE ? OR display_name LIKE ? ORDER BY created_at DESC", (f"%{search}%", f"%{search}%"))
    else:
        cursor.execute("SELECT * FROM users ORDER BY created_at DESC")
    users = cursor.fetchall()
    conn.close()

    return render_template("users.html", users=users, search=search)

@app.route("/admin/quran", methods=["GET", "POST"])
def admin_quran():
    conn = get_db()
    cursor = conn.cursor()

    if request.method == "POST":
        action = request.form.get("action")
        if action == "add_verse":
            surah_num = int(request.form.get("surah_number", 1))
            verse_num = int(request.form.get("verse_number", 1))
            text_ar = request.form.get("text_arabic", "").strip()
            text_bn = request.form.get("text_bengali", "").strip()
            audio_url = request.form.get("audio_url", "").strip()

            if text_ar and text_bn:
                cursor.execute("""
                INSERT INTO verses (surah_number, verse_number, text_arabic, text_bengali, audio_url)
                VALUES (?, ?, ?, ?, ?)
                """, (surah_num, verse_num, text_ar, text_bn, audio_url))
                cursor.execute("""
                INSERT INTO audit_logs (admin_username, action, target, details)
                VALUES (?, 'Add Verse', ?, ?)
                """, (session.get("username", "Admin"), f"Surah {surah_num}:{verse_num}", "Verse added via Web Admin"))
                conn.commit()
                flash(f"সূরা {surah_num} এর আয়াত {verse_num} যুক্ত হয়েছে।", "success")

    cursor.execute("SELECT * FROM surahs ORDER BY number ASC")
    surahs = cursor.fetchall()

    cursor.execute("SELECT v.*, s.name_bengali as surah_name FROM verses v JOIN surahs s ON v.surah_number = s.number ORDER BY v.surah_number ASC, v.verse_number ASC LIMIT 50")
    verses = cursor.fetchall()
    conn.close()

    return render_template("quran.html", surahs=surahs, verses=verses)

@app.route("/admin/payments", methods=["GET", "POST"])
def admin_payments():
    conn = get_db()
    cursor = conn.cursor()

    if request.method == "POST":
        req_id = request.form.get("request_id")
        decision = request.form.get("decision") // "approve" or "reject"

        cursor.execute("SELECT * FROM payment_requests WHERE id = ?", (req_id,))
        req_item = cursor.fetchone()

        if req_item:
            new_status = "Approved" if decision == "approve" else "Rejected"
            cursor.execute("UPDATE payment_requests SET status = ? WHERE id = ?", (new_status, req_id))

            if decision == "approve":
                cursor.execute("UPDATE users SET is_premium = 1, premium_plan = ? WHERE id = ?", (req_item["plan_name"], req_item["user_id"]))

            cursor.execute("""
            INSERT INTO audit_logs (admin_username, action, target, details)
            VALUES (?, ?, ?, ?)
            """, (session.get("username", "Admin"), f"Payment {new_status}", f"TxID: {req_item['transaction_id']}", f"User: {req_item['username']}"))
            conn.commit()
            flash(f"পেমেন্ট রিকোয়েস্ট {new_status} সম্পন্ন।", "success")

    cursor.execute("SELECT * FROM payment_requests ORDER BY created_at DESC")
    requests_list = cursor.fetchall()
    conn.close()

    return render_template("payments.html", requests=requests_list)

@app.route("/admin/audit-logs")
def admin_audit_logs():
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    logs = cursor.fetchall()
    conn.close()
    return render_template("audit_logs.html", logs=logs)

# ==================== CLIENT REST APIS ====================

@app.route("/api/surahs", methods=["GET"])
def api_surahs():
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM surahs ORDER BY number ASC")
    surahs = [dict(row) for row in cursor.fetchall()]
    conn.close()
    return jsonify({"success": True, "count": len(surahs), "surahs": surahs})

@app.route("/api/surah/<int:num>", methods=["GET"])
def api_surah_detail(num):
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM surahs WHERE number = ? LIMIT 1", (num,))
    surah = cursor.fetchone()
    if not surah:
        conn.close()
        return jsonify({"success": False, "error": "Surah not found"}), 404

    cursor.execute("SELECT * FROM verses WHERE surah_number = ? ORDER BY verse_number ASC", (num,))
    verses = [dict(row) for row in cursor.fetchall()]
    conn.close()
    return jsonify({"success": True, "surah": dict(surah), "verses": verses})

@app.route("/api/search", methods=["GET"])
def api_search():
    q = request.args.get("q", "").strip()
    if not q:
        return jsonify({"success": True, "results": []})

    conn = get_db()
    cursor = conn.cursor()
    cursor.execute("""
    SELECT v.*, s.name_bengali as surah_name 
    FROM verses v 
    JOIN surahs s ON v.surah_number = s.number 
    WHERE v.text_bengali LIKE ? OR v.text_arabic LIKE ? 
    LIMIT 50
    """, (f"%{q}%", f"%{q}%"))
    results = [dict(row) for row in cursor.fetchall()]
    conn.close()
    return jsonify({"success": True, "query": q, "results": results})

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    host = os.environ.get("HOST", "127.0.0.1")
    print(f"Quranify Islamic Server starting on http://{host}:{port}")
    app.run(host=host, port=port, debug=False)
