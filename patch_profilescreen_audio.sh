sed -i '/import androidx.compose.ui.unit.dp/a \
import android.Manifest\
import androidx.activity.compose.rememberLauncherForActivityResult\
import androidx.activity.result.contract.ActivityResultContracts\
import androidx.core.content.ContextCompat\
import android.content.pm.PackageManager\
import androidx.compose.material.icons.filled.Mic\
import androidx.compose.material.icons.filled.Stop\
import com.example.util.AudioRecorderHelper\
import kotlinx.coroutines.launch\
' app/src/main/java/com/example/ui/screens/ProfileScreen.kt
