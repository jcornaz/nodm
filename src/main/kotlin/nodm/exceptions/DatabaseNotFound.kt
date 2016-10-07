package nodm.exceptions

class DatabaseNotFound(server: String, databae: String) : Exception("Database \"$databae\" not found on \"$server\"")