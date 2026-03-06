package com.forday.app.core.logger.timber

import timber.log.Timber

class TimberInitializer(
    private vararg val trees: Timber.Tree,
) {
    fun execute() {
        if (trees.isNotEmpty()) {
            Timber.plant(*trees)
        }
    }
}