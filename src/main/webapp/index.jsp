<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#5a5ce2">
    <title>StudentOS — Your Life. Your Skills. Your Opportunities.</title>
    <link rel="icon" type="image/png" href="/favicon.png">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@600;700;800&display=swap');

        :root { --ink: #12213f; --muted: #61708a; --brand: #5a5ce2; --brand-deep: #4548c4; --line: #e6eaf3; }
        * { box-sizing: border-box; }
        body {
            min-height: 100vh;
            margin: 0;
            overflow-x: hidden;
            color: var(--ink);
            background: radial-gradient(circle at 14% 6%, rgba(97, 204, 245, .18), transparent 25rem), radial-gradient(circle at 88% 88%, rgba(113, 100, 235, .16), transparent 27rem), #f8faff;
            font-family: 'DM Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        }
        .landing { position: relative; display: flex; flex-direction: column; min-height: 100vh; padding: 26px clamp(24px, 6vw, 90px) 36px; }
        .landing::before { position: absolute; inset: 0; z-index: 0; background-image: linear-gradient(rgba(99, 113, 153, .035) 1px, transparent 1px), linear-gradient(90deg, rgba(99, 113, 153, .035) 1px, transparent 1px); background-size: 52px 52px; content: ''; mask-image: linear-gradient(to bottom, rgba(0,0,0,.45), transparent 60%); }
        .topbar, .hero, .footer-note { position: relative; z-index: 1; }
        .topbar { display: flex; align-items: center; justify-content: space-between; }
        .brand { display: inline-flex; align-items: center; gap: 10px; color: var(--ink); font-family: 'Plus Jakarta Sans', sans-serif; font-size: 16px; font-weight: 800; letter-spacing: -.65px; text-decoration: none; }
        .brand-mark { display: inline-block; width: 37px; height: 37px; background: url('/assets/studentos-logo-transparent.png') center / contain no-repeat; filter: drop-shadow(0 7px 12px rgba(58, 74, 201, .2)); }
        .topbar-link { padding: 9px 13px; color: #4d5b76; border: 1px solid var(--line); border-radius: 10px; font-size: 13px; font-weight: 700; text-decoration: none; transition: border-color .2s, color .2s, transform .2s; }
        .topbar-link:hover { color: var(--brand); border-color: #cccdf8; transform: translateY(-1px); }
        .hero { display: grid; flex: 1; place-items: center; padding: 56px 0 40px; text-align: center; }
        .hero-copy { max-width: 790px; }
        .logo-orbit { position: relative; display: grid; width: 96px; height: 96px; margin: 0 auto 25px; place-items: center; border-radius: 30px; background: linear-gradient(145deg, #ffffff, #f4f5ff); border: 1px solid rgba(223, 228, 245, .9); box-shadow: 0 20px 45px rgba(41, 56, 111, .12), inset 0 1px 0 rgba(255,255,255,.9); }
        .logo-orbit::before { position: absolute; inset: -8px; border: 1px solid rgba(91, 92, 226, .18); border-radius: 35px; content: ''; }
        .logo-orbit::after { width: 67px; height: 67px; content: ''; background: url('/assets/studentos-logo-transparent.png') center / contain no-repeat; filter: drop-shadow(0 9px 14px rgba(50, 66, 196, .24)); }
        .eyebrow { margin-bottom: 14px; color: var(--brand); font-size: 11px; font-weight: 800; letter-spacing: .13em; text-transform: uppercase; }
        h1 { max-width: 740px; margin: 0 auto; color: #10203e; font-family: 'Plus Jakarta Sans', sans-serif; font-size: clamp(42px, 6vw, 68px); font-weight: 800; letter-spacing: -3.6px; line-height: 1.04; }
        .tagline { margin: 19px auto 0; color: #465671; font-size: clamp(17px, 2vw, 20px); font-weight: 600; }
        .description { max-width: 620px; margin: 18px auto 0; color: var(--muted); font-size: 16px; line-height: 1.7; }
        .actions { display: flex; flex-wrap: wrap; justify-content: center; gap: 11px; margin-top: 30px; }
        .btn { display: inline-flex; align-items: center; justify-content: center; min-height: 46px; padding: 12px 18px; border: 1px solid transparent; border-radius: 12px; font-size: 14px; font-weight: 800; text-decoration: none; transition: transform .2s, box-shadow .2s, background .2s; }
        .btn:hover { transform: translateY(-2px); }
        .btn-primary { color: #fff; background: linear-gradient(135deg, #6a6ded, #4a4ac9); box-shadow: 0 12px 22px rgba(78, 78, 200, .25); }
        .btn-primary:hover { background: linear-gradient(135deg, #5c5fe4, #3f40b8); box-shadow: 0 15px 26px rgba(78, 78, 200, .32); }
        .btn-secondary { color: #42516d; background: rgba(255,255,255,.82); border-color: var(--line); box-shadow: 0 4px 10px rgba(32, 48, 77, .04); }
        .btn-secondary:hover { border-color: #cfd5e7; background: #fff; }
        .value-strip { display: flex; flex-wrap: wrap; justify-content: center; gap: 10px 20px; margin-top: 31px; color: #6e7a90; font-size: 12px; font-weight: 700; }
        .value-strip span { display: inline-flex; align-items: center; gap: 7px; }
        .value-strip i { display: inline-block; width: 5px; height: 5px; background: #56c6a3; border-radius: 50%; box-shadow: 0 0 0 4px rgba(86, 198, 163, .13); }
        .footer-note { color: #9aa4b7; font-size: 11px; font-weight: 600; text-align: center; }
        @media (max-width: 620px) { .landing { padding: 20px 20px 28px; } .topbar-link { display: none; } .hero { padding: 46px 0 28px; } h1 { letter-spacing: -2.5px; } .description { font-size: 15px; } .actions { flex-direction: column; } .btn { width: 100%; } .value-strip { justify-content: flex-start; } }
    </style>
</head>
<body>
    <main class="landing">
        <header class="topbar">
            <a class="brand" href="/"><span class="brand-mark" aria-hidden="true"></span>StudentOS</a>
            <a class="topbar-link" href="/auth/signin">Sign in</a>
        </header>

        <section class="hero" aria-labelledby="landing-title">
            <div class="hero-copy">
                <div class="logo-orbit" aria-hidden="true"></div>
                <div class="eyebrow">Your student workspace</div>
                <h1 id="landing-title">Build your student life with purpose.</h1>
                <p class="tagline">Your life. Your skills. Your opportunities.</p>
                <p class="description">One focused place to organize your goals, share what you know, discover talented classmates, and turn everyday progress into meaningful opportunities.</p>
                <div class="actions">
                    <a href="/auth/signin" class="btn btn-primary">Get started</a>
                    <a href="/skills/discover" class="btn btn-secondary">Explore talent</a>
                </div>
                <div class="value-strip"><span><i></i>Plan with clarity</span><span><i></i>Grow your skills</span><span><i></i>Connect with peers</span></div>
            </div>
        </section>

        <div class="footer-note">StudentOS · Designed for focused student progress</div>
    </main>
</body>
</html>
