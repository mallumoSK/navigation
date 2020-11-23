@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package tk.mallumo.layout.inflater

import java.io.File

/**
 * Class provides writing generated classes,logs, ... into files
 *
 * @param directory target directory for generated files
 * @param rootPackage every ksp compiler must have unique package name, to prevent problems
 *
 * @see CodeWriter.add
 * @see CodeWriter.write
 */
class CodeWriter(
    private val directory: File,
    private val rootPackage: String
) {

    /**
     * returns number of files which will be written
     */
    val filesCount: Int get() = builders.size

    private val builders = hashMapOf<String, HashMap<String, BuilderEntry>>()

    /**
     * Entry which store generated data,
     *
     * every entry is unique with hual primary key packageName and fileName
     *
     * you can:
     * * write multiple times into one entry (builder:StringBuilder)
     * * add duplicates imports into one entry (imports:HashSet)
     *
     * @param packageName the first primary identifier of entry, must start with ``CodeWriter.rootPackage``
     * @param fileName the second primary  identifier of entry, must contains sufix '.kt', '.log', ...
     * @param builder contains whole soucecode of entry expect package name and imports
     * @param imports hash-map of imports whitch need class to live etc. 'java.io.File'
     *
     * @see CodeWriter.rootPackage
     */
    private inner class BuilderEntry(
        val packageName: String,
        val fileName: String,
        val builder: StringBuilder = StringBuilder(),
        val imports: HashSet<String> = hashSetOf()
    ) {

        /**
         * Generate system class / log file with fully qualified apsolute path
         *
         * @param directory expect root of ksp directory ``CodeWriter.directory``
         * @see CodeWriter.directory
         */
        fun file(directory: File): File =
            File("${directory.absolutePath}/${packageName.replace(".", "/")}/$fileName").also {
                if (!it.parentFile.exists()) it.parentFile.mkdirs()
            }

        /**
         * Write generated sources ''BuilderEntry.fileSource'' into target file ''BuilderEntry.file''
         *
         * @param kspDir expect root of ksp directory
         *
         * @see BuilderEntry.fileSource
         * @see BuilderEntry.file
         */
        fun write(kspDir: File) {
            file(kspDir).writeText(fileSource)
        }

        /**
         * Join whole sources into one string
         */
        val fileSource: String
            get() = """@file:Suppress("unused")
                
package $packageName

${imports.joinToString("\n") { "import $it" }}

$builder"""

    }

    /**
     * Extract unique file builder entry by identifiers packageName and fileName
     *
     * if currently entry no exist, new ic created
     *
     * @param packageName the first primary identifier of entry, must start with ``CodeWriter.rootPackage``
     * @param fileName the second primary  identifier of entry, must contains sufix '.kt', '.log', ...
     */
    private fun getBuilderEntry(packageName: String, fileName: String): BuilderEntry {
        if (!packageName.startsWith(rootPackage)) throw RuntimeException("this package '$packageName' is NOT starting by root package '$rootPackage'")

        val packageFiles = builders.getOrPut(packageName) {
            hashMapOf()
        }
        return packageFiles.getOrPut(fileName) {
            BuilderEntry(packageName, fileName)
        }
    }

    /**
     * Add sources into entry file
     *
     * @see BuilderEntry
     */
    fun add(
        packageName: String,
        fileName: String,
        imports: List<String> = listOf(),
        body: StringBuilder.() -> Unit
    ) {
        with(getBuilderEntry(packageName, fileName)) {
            this.imports.addAll(imports)
            body(builder)
        }

    }

    /**
     * This function must be called after all code is generated and ready for writing,
     *
     * * prepare new files
     * * collect all old files
     * * remove old files, which are not in use, but only if parameter ''deleteOld == true''
     * * write new files
     */
    fun write(deleteOld: Boolean) {
        val libDirectory = File("${directory.absolutePath}/${rootPackage.replace(".", "/")}")
        val oldFiles = libDirectory.walkTopDown().filter { it.isFile }.toList()
        val generated = builders.values.map { it.values }.flatten()
        val newFile = generated.map { it.file(directory) }

        if (deleteOld) {
            oldFiles.filter { old -> newFile.none { new -> old.absolutePath == new.absolutePath } }
                .forEach { it.delete() }
        }

        generated.forEach { entry ->
            entry.write(directory)
        }
    }
}