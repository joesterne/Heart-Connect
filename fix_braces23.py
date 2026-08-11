with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "r") as f:
    priv = f.read()
import re
priv = re.sub(r'\}\s*\}\s*@Composable\s*fun SecureMessageBubble', '}\n@Composable\nfun SecureMessageBubble', priv)

with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "w") as f:
    f.write(priv)
