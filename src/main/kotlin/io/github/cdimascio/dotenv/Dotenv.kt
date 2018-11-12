/**
 * Copyright (c) Carmine DiMascio 2017 - 2018
 * License: MIT
 */
package io.github.cdimascio.dotenv

import io.github.cdimascio.dotenv.internal.DotenvParser
import io.github.cdimascio.dotenv.internal.DotenvReader

/**
 * Dotenv
 * @see <a href="https://github.com/cdimascio/java-dotenv">The complete dotenv documentation</a>
 */
abstract class Dotenv {
    /**
     * The dotenv instance
     */
    companion object Instance {
        /**
         * Configure dotenv
         * @return A dotenv builder
         */
        @JvmStatic fun configure(): DotenvBuilder = DotenvBuilder()

        /**
         * Load the the contents of .env into the virtual nvironment.
         * Environment variables in the host environment override those in .env
         */
        @JvmStatic fun load(): Dotenv = DotenvBuilder().load()
    }

    /**
     * Returns the value for the specified environment variable
     * @param get The environment variable name
     */
    operator abstract fun get(envVar: String): String?
}

class DotEnvException(message: String) : Exception(message)

/**
 * Constructs a new DotenvBuilder
 */
class DotenvBuilder internal constructor() {
    private var filename = ".env"
    private var directoryPath = ""
    private var throwIfMissing = true
    private var throwIfMalformed = true

    /**
     * Set the directory containing the .env file
     * @param directoryPath The path
     */
    fun directory(path: String = directoryPath): DotenvBuilder {
        directoryPath = path
        return this
    }

    /**
     * Set the name of the .env, if not .env
     * @param filename The filename
     */
    fun filename(name: String = ".env"): DotenvBuilder {
        filename = name
        return this
    }

    /**
     * Do not throw an error when .env is not presents
     */
    fun ignoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    /**
     * Do not throw an error when .env is malformed
     */
    fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    /**
     * Load the contents of .env into the virtual environment
     */
    fun load(): Dotenv {
        val reader = DotenvParser(
                DotenvReader(directoryPath, filename),
                throwIfMalformed,
                throwIfMissing)
        val env = reader.parse()
        return DotenvImpl(env)
    }
}

private class DotenvImpl(envVars: List<Pair<String, String>>) : Dotenv() {
    private val map = envVars.associateBy({ it.first }, { it.second })

    override fun get(envVar: String): String? = System.getenv(envVar) ?: map[envVar]
}