for filename in ["app/src/main/java/com/example/ui/screens/FavoritesScreen.kt", "app/src/main/java/com/example/ui/navigation/AppNavigation.kt"]:
    with open(filename, "r") as f:
        text = f.read()
    c = 0
    for ch in text:
        if ch == '{': c += 1
        elif ch == '}': c -= 1
    print(f"{filename}: {c}")
