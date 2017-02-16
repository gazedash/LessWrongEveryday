package com.shoegazerwithak.lesswrongeveryday.ui

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker

import com.shoegazerwithak.lesswrongeveryday.R

import java.util.Date
import java.util.GregorianCalendar

class TimePreference @JvmOverloads constructor(ctxt: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.dialogPreferenceStyle) : DialogPreference(ctxt, attrs, defStyle) {
    private val calendar: GregorianCalendar? = GregorianCalendar()
    private var picker: TimePicker? = null

    init {
        setPositiveButtonText(R.string.set)
        setNegativeButtonText(R.string.cancel)
    }

    override fun onCreateDialogView(): View = TimePicker(context)
    //        picker = TimePicker(context)
    //        return picker as TimePicker

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker!!.currentHour = calendar!!.get(GregorianCalendar.HOUR_OF_DAY)
        picker!!.currentMinute = calendar.get(GregorianCalendar.MINUTE)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar!!.set(GregorianCalendar.HOUR_OF_DAY, picker!!.currentHour)
            calendar.set(GregorianCalendar.MINUTE, picker!!.currentMinute)
            summary = summary
            if (callChangeListener(calendar.timeInMillis)) {
                persistLong(calendar.timeInMillis)
                notifyChanged()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getString(index)!!

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        when {
            restoreValue -> when (defaultValue) {
                null -> calendar!!.timeInMillis = getPersistedLong(System.currentTimeMillis())
                else -> calendar!!.timeInMillis = java.lang.Long.parseLong(getPersistedString(defaultValue as String?))
            }
            else -> when (defaultValue) {
                null -> calendar!!.timeInMillis = System.currentTimeMillis()
                else -> calendar!!.timeInMillis = java.lang.Long.parseLong(defaultValue as String?)
            }
        }
        summary = summary
    }

    override fun getSummary() = if (calendar == null) {
        null
    } else DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
}