import requests
import re
import os

session = requests.Session()
base_url = "https://student-os-v2.onrender.com"

print("1. Loading signin page...")
r1 = session.get(f"{base_url}/auth/signin")
csrf_match = re.search(r'name="csrfToken"\s+value="([^"]+)"', r1.text)
if not csrf_match:
    print("Error: Could not find CSRF token on login page.")
    exit(1)
csrf_token = csrf_match.group(1)

print("2. Logging in...")
login_data = {
    "email": "lku199776@gmail.com",
    "password": "Jaxk#*168",
    "csrfToken": csrf_token
}
r2 = session.post(f"{base_url}/auth/signin", data=login_data)
if "error" in r2.url or "signin" in r2.url:
    print("Error: Login failed. Check credentials.")
    exit(1)
print("Login successful!")

print("3. Loading profile page...")
r3 = session.get(f"{base_url}/profile")
csrf_match = re.search(r'name="csrfToken"\s+value="([^"]+)"', r3.text)
if not csrf_match:
    print("Error: Could not find CSRF token on profile page.")
    exit(1)
profile_csrf = csrf_match.group(1)

print("4. Creating test image...")
with open("dummy.jpg", "wb") as f:
    f.write(b"\xFF\xD8\xFF\xE0\x00\x10JFIF\x00\x01\x01\x01\x00H\x00H\x00\x00\xFF\xDB\x00C\x00\xFF\xD9")

print("5. Submitting profile upload...")
upload_url = f"{base_url}/profile/save?csrfToken={profile_csrf}"
files = {
    'avatar': ('dummy.jpg', open('dummy.jpg', 'rb'), 'image/jpeg')
}
data = {
    "csrfToken": profile_csrf,
    "firstName": "Lan",
    "lastName": "Ku",
    "bio": "Testing the upload feature.",
    "university": "Student OS",
    "major": "CS",
    "availabilityStatus": ""
}
r4 = session.post(upload_url, data=data, files=files)

print(f"Upload Response URL: {r4.url}")
if "saved=1" in r4.url:
    print("SUCCESS: Profile and image saved successfully!")
elif "error" in r4.url:
    print("FAILED: Server returned an error.")
else:
    print("FAILED: Unknown response.")
