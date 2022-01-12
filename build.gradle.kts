group = "io.github.mslxl"

fun File.createIfNotExists(): File {
    if (!exists()) {
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        createNewFile()
    } else {
        writeText("")
    }
    return this
}

val copyLiceList = task("copyLiceList", type = Jar::class) {
    from("../licenses/${project.name}.list") {
        into("licenses/list.txt")
    }
}

fun configInstallLicense(project: Project) {
    project.afterEvaluate {
        tasks.getByName<Jar>("jar") {
            val liceListFile = File("./licenses/${project.name}.list").createIfNotExists()
            val bufferWriter = liceListFile.bufferedWriter()
            from("../licenses/common") {
                eachFile {
                    bufferWriter.write(file.name)
                    bufferWriter.write("\n")
                    bufferWriter.flush()
                }
                into("licenses")
            }
            val sourcePath = when (project.name) {
                "desktop" -> "../licenses/desktop"
                else -> error("Unrecognized project ${project.name}")
            }
            from(sourcePath) {
                eachFile {
                    bufferWriter.write(file.name)
                    bufferWriter.write("\n")
                    bufferWriter.flush()
                }
                into("licenses")
            }
            from("../licenses/${project.name}.list") {
                rename {
                    "list.txt"
                }
                into("licenses")
            }
            doLast {
                bufferWriter.close()
            }
        }
    }
}

project("desktop") {
    configInstallLicense(this)
}

//TODO 会有的
//project("android"){
//    configInstallLicense(this)
//}
