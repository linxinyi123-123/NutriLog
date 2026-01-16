package com.example.nutrilog.analysis.exception

class DataNotFoundException(message: String) : Exception(message) {
    constructor() : this("Required data not found")
}