plugins {
    id("bpm.build-logic")
}

// Thanks BanManager, but your log message is now my property
logger.lifecycle("""
*******************************************
 You are building ${rootProject.name}!
 If you encounter trouble:
 1) Try running "build" in a separate Gradle run
 2) Use gradlew and not gradle
 3) If you still need help, open an issue on GitHub!
 Output files will be in [subproject]/build/libs
*******************************************
""")
