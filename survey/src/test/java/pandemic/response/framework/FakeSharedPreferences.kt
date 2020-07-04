package pandemic.response.framework

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {
    val values = mutableMapOf<String, Any>()

    override fun contains(key: String?): Boolean = values.contains(key)

    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        values[key] as? Boolean ?: defValue

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String, defValue: Int): Int {
        return values[key] as Int
    }

    override fun getAll(): MutableMap<String, *> {
        TODO("Not yet implemented")
    }

    override fun edit(): SharedPreferences.Editor {
        return object : SharedPreferences.Editor {
            override fun clear(): SharedPreferences.Editor {
                TODO("Not yet implemented")
            }

            override fun putLong(key: String, value: Long): SharedPreferences.Editor {
                values[key] = value
                return this
            }

            override fun putInt(key: String, value: Int): SharedPreferences.Editor {
                values[key] = value
                return this
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                TODO("Not yet implemented")
            }

            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
                values[key] = value
                return this
            }

            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?
            ): SharedPreferences.Editor {
                TODO("Not yet implemented")
            }

            override fun commit(): Boolean {
                TODO("Not yet implemented")
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                TODO("Not yet implemented")
            }

            override fun apply() = Unit

            override fun putString(key: String, value: String?): SharedPreferences.Editor {
                values[key] = value ?: ""
                return this
            }
        }
    }

    override fun getLong(key: String, defValue: Long): Long {
        return values[key] as Long
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        TODO("Not yet implemented")
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun getString(key: String, defValue: String?): String {
        return values[key] as String
    }
}