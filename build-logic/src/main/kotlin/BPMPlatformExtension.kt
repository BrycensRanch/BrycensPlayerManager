import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.property

abstract class BPMPlatformExtension(project: Project) {
  val jarTask: Property<AbstractArchiveTask> = project.objects.property()
}
