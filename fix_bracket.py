with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    lines = f.readlines()

# Line 475 is 0-indexed 474
if lines[474].strip() == "}":
    del lines[474]
    
with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.writelines(lines)
