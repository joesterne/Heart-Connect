with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

content = content.replace('android:theme="@style/Theme.MyApplication">', 'android:networkSecurityConfig="@xml/network_security_config"\n        android:theme="@style/Theme.MyApplication">')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
