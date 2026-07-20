with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    content = f.read()

old_map = """fun CentersMap() {
    val defaultLocation = LatLng(37.7749, -122.4194) // Default to SF
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val centers = listOf(
        Pair(LatLng(37.7694, -122.4862), "UCSF Medical Center"),
        Pair(LatLng(37.4300, -122.1700), "Stanford Hospital"),
        Pair(LatLng(37.7944, -122.4000), "Community Support Group A")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Find nearby transplant centers and support groups. (Requires Maps API Key in Settings)",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Card(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 16.dp), shape = RoundedCornerShape(24.dp)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                centers.forEach { (latLng, title) ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = title,
                        snippet = "Transplant Support"
                    )
                }
            }
        }
    }
}"""

new_map = """fun CentersMap() {
    val defaultLocation = LatLng(37.7749, -122.4194) // Default to SF
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val centers = listOf(
        Pair(LatLng(37.7694, -122.4862), "UCSF Medical Center - Support Group"),
        Pair(LatLng(37.4300, -122.1700), "Stanford Hospital - Patient Meetup"),
        Pair(LatLng(37.7944, -122.4000), "Community Support Group A"),
        Pair(LatLng(37.8044, -122.2711), "Oakland Transplant Survivor Event"),
        Pair(LatLng(37.3382, -121.8863), "San Jose Heart Health Walk")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Find nearby transplant centers, support groups, and community events on the map below.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Card(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 16.dp), shape = RoundedCornerShape(24.dp)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                centers.forEach { (latLng, title) ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = title,
                        snippet = "Transplant Support & Events"
                    )
                }
            }
        }
    }
}"""

content = content.replace(old_map, new_map)

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "w") as f:
    f.write(content)
