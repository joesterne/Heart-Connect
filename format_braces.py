with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
indent = 0
for line in lines:
    stripped = line.strip()
    if stripped.startswith("}") or stripped.startswith(")") or stripped.startswith("]"):
        indent = max(0, indent - 1)
        
    new_lines.append("    " * indent + stripped + "\n")
    
    # Very rudimentary, doesn't handle multiple braces on one line perfectly if nested
    # But good enough for visual inspection
    if (not stripped.startswith("//")) and ("{" in line or "}" in line):
        pass # well we could just count
        
with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt.formatted", "w") as f:
    f.writelines(new_lines)
