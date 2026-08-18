<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student OS - Your Life. Your Skills. Your Opportunities.</title>
    <style>
        body {
            font-family: 'Inter', system-ui, -apple-system, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #fafafa;
            color: #111;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            text-align: center;
        }
        h1 { font-size: 3rem; margin-bottom: 0.5rem; }
        p { font-size: 1.2rem; color: #555; max-width: 600px; line-height: 1.6; }
        .btn {
            display: inline-block;
            margin-top: 2rem;
            padding: 0.8rem 1.5rem;
            background-color: #111;
            color: #fff;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 500;
            transition: background-color 0.2s;
        }
        .btn:hover { background-color: #333; }
    </style>
</head>
<body>
    <h1>STUDENT OS</h1>
    <p>Your Life. Your Skills. Your Opportunities.</p>
    <p>A unified platform where students organize their lives, exchange knowledge, and discover opportunities.</p>
    <div>
        <a href="auth/signin" class="btn">Get Started</a>
        <a href="#explore" class="btn" style="background-color: #e0e0e0; color: #111; margin-left: 1rem;">Explore Platform</a>
    </div>
</body>
</html>
