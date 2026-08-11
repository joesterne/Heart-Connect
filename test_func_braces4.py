def check(path, start_line, end_line):
    with open(path) as f:
        lines = f.readlines()
    text = "".join(lines[start_line-1:end_line])
    return text.count("{") - text.count("}")

print("PrivateChatScreen", check("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", 43, 204))
