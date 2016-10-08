package nodm.exceptions

class DatabaseNotFound(name: String) : Exception("Database \"$name\" not found")