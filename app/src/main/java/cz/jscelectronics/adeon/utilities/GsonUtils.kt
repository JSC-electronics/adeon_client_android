package cz.jscelectronics.adeon.utilities

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Custom type adapter to de/serialize Uri links. If Uri points to internal resource image,
 * we want to have it exported. Otherwise Uri will be set to null.
 */
class GsonUriTypeAdapter: TypeAdapter<Uri>() {

    override fun read(input: JsonReader?): Uri? {
        input?.let {
            if (input.peek() == JsonToken.NULL) {
                input.nextNull()
                return null
            }

            return Uri.parse(input.nextString())
        }

        return null
    }

    override fun write(output: JsonWriter?, value: Uri?) {
        var uriToWrite: String? = null

        value?.toString()?.let {
            if (it.contains(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                uriToWrite = it
            }
        }

        output?.value(uriToWrite)
    }

}
