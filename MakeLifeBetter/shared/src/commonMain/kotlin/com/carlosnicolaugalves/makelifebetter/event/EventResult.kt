package com.carlosnicolaugalves.makelifebetter.event

import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection

sealed class EventResult {
    data object Idle : EventResult()
    data object Loading : EventResult()
    data class Success(val events: List<Event>) : EventResult()
    data class Error(val message: String) : EventResult()
}

sealed class EventSectionsResult {
    data object Idle : EventSectionsResult()
    data object Loading : EventSectionsResult()
    data class Success(val sections: List<EventSection>) : EventSectionsResult()
    data class Error(val message: String) : EventSectionsResult()
}
