for file in app/src/main/java/com/example/ui/screens/*.kt; do
    if [[ "$file" != *"AuthScreen.kt"* && "$file" != *"DashboardScreen.kt"* ]]; then
        # Replace the fully qualified name back to Icons.Default.Menu just in case, but keep it if it works with the import
        sed -i 's/androidx.compose.material.icons.Icons.Default.Menu/Icons.Default.Menu/g' "$file"
        
        # Add the import
        sed -i '/import androidx.compose.material.icons.Icons/a \import androidx.compose.material.icons.filled.Menu' "$file"
    fi
done
