with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "r") as f:
    text = f.read()

import re
text = re.sub(r'\}\s*@Composable\s*fun SecureMessageBubble', '}\n}\n}\n@Composable\nfun SecureMessageBubble', text)

with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "w") as f:
    f.write(text)
