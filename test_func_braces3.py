def check(path, start_line, end_line):
    with open(path) as f:
        lines = f.readlines()
    
    text = "".join(lines[start_line-1:end_line])
    return text.count("{") - text.count("}")

print("CommunityScreen", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 49, 155))
print("CommunityFeed", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 157, 273))
print("PostCard", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 275, 335))
print("GroupCard", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 337, 455))
print("QAFeed", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 457, 519))
print("QACard", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 521, 604))
print("CentersMap", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 606, 650))
print("ScheduleFeed", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 652, 672))
print("ScheduledSessionCard", check("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", 674, 777))
