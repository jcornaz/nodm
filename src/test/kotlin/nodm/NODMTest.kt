package nodm

import com.toolable.notes.stub.model.DatabaseStub
import com.toolable.notes.stub.model.DocumentStub
import com.toolable.notes.stub.model.Unid
import org.junit.*


class NODMTest {

    class MyClass

    lateinit var db: DatabaseStub

    @Before
    fun setup() {
        db = DatabaseStub()
        NODM.connect(db.implementation)
    }

    @After()
    fun tearDown() {
        NODM.disconnect()
    }

    @Test
    fun testCreateInstance() {
        val doc = DocumentStub(db)

        Assert.assertNotNull(NODM[doc.unid.toString(), MyClass::class])
    }

    @Test
    fun testCreateInstanceWhenNotExists() {
        Assert.assertNull(NODM[Unid.generate().toString(), MyClass::class])
    }
}