package net.kaleidos.taiga.builder

import net.kaleidos.domain.EstimationPoint
import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.SafeJson

class EstimationPointBuilder implements TaigaEntityBuilder<EstimationPoint>, SafeJson {

    EstimationPoint build(Map json, Project project) {
        def estimationPoint = new EstimationPoint()

        estimationPoint.with {
            name = json.name
            value = nullSafe(json.value)
        }

        estimationPoint
    }
}