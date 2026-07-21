with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

content = content.replace('''        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:networkSecurityConfig="@xml/network_security_config"
        android:theme="@style/Theme.MyApplication">''', '''        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication">''')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
