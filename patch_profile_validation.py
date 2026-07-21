with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    content = f.read()

old_validation = '''                        if (name.isBlank() || age == null || location.isBlank() || medicalHistory.isBlank()) {
                            Toast.makeText(context, "Please fill out all fields with valid information.", Toast.LENGTH_LONG).show()
            } else {
                viewModel.saveProfile(name, age, location, medicalHistory, aboutMe, journeyPhase, isAvailableForMentorship)
                isEditMode = false
            }'''

new_validation = '''                        var errorMessage = ""
                        if (name.trim().length < 2) {
                            errorMessage = "Name must be at least 2 characters."
                        } else if (age == null || age !in 0..120) {
                            errorMessage = "Please enter a valid age between 0 and 120."
                        } else if (location.trim().length < 2) {
                            errorMessage = "Please enter a valid location."
                        } else if (medicalHistory.trim().length < 5) {
                            errorMessage = "Please provide more detail in your medical history."
                        }
                        
                        if (errorMessage.isNotEmpty()) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.saveProfile(name.trim(), age!!, location.trim(), medicalHistory.trim(), aboutMe.trim(), journeyPhase, isAvailableForMentorship)
                            isEditMode = false
                        }'''

content = content.replace(old_validation, new_validation)

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(content)
