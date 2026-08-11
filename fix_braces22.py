with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    comm = f.read()

import re

# GroupCard has -2 braces (too few)
# Let's fix GroupCard (lines 339 to 457 roughly)
comm = re.sub(r'\}\s*\}\s*\}\s*\}\s*\}\s*\}\s*@OptIn', '}\n}\n}\n}\n}\n}\n}\n}\n@OptIn', comm)

# We should probably just count and append to the end of functions.
# Actually, the safest way is to print the end of these functions to see what they look like.

