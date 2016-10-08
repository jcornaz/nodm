package nodm

import com.toolable.notes.stub.model.DatabaseStub
import com.toolable.notes.stub.model.DocumentStub
import com.toolable.notes.stub.model.Unid
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class NODMTest {

    class MyClass {

        val testField: String = ""

        @NotesItem(name = "notesName")
        val intValue: Int = 0

        @NotesTransientItem
        val transientField: String = "content"
    }

    lateinit var db: DatabaseStub

    @Before
    fun setup() {
        db = DatabaseStub()
        NODM.connect(db.implementation)
    }

    @Test
    fun testRead() {
        val doc = DocumentStub(db)

        doc["notesName"] = 42
        doc["testField"] = "hello world"

        val instance: MyClass? = NODM[doc.unid.toString()]

        Assert.assertNotNull(instance); instance!!
        Assert.assertEquals(42, instance.intValue)
        Assert.assertEquals("hello world", instance.testField)
        Assert.assertEquals("content", instance.transientField)
    }

    @Test
    fun testReadNonExistentDocument() {
        Assert.assertNull(NODM[Unid.generate().toString(), MyClass::class])
    }
}