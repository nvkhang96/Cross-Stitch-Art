package com.a9itgroup.crossstitchart

import android.util.Log

class MyColor(private var color: Int) {
    private var count: Int = 0
    private var limit: Int

    init {
        this.limit=0
    }

    fun setLimit(limit: Int){
        this.limit = limit
    }

    fun getLimit() = limit

    fun increaseCount(){
        count++
        Log.d("DEBUGK","I "+color.toString()+" "+count.toString())
    }

    fun increaseCount(i: Int){
        count+=i
        Log.d("DEBUGK","I "+color.toString()+" "+count.toString())
    }

    fun decreaseCount(){
        count--
        Log.d("DEBUGK","D "+color.toString()+" "+count.toString())
    }

    fun decreaseCount(i: Int){
        count-=i
        Log.d("DEBUGK","D "+color.toString()+" "+count.toString())
    }

    fun getCount() =  if (limit == 0) {
            count
        } else {
            limit - count
        }

    fun isUsable(use: Int) = limit==0||(count+use)<=limit

    fun isEqual(color: Int): Boolean {
        Log.d("DEBUGK"," "+Integer.toHexString(this.color)+" "+ Integer.toHexString(color))
        if (Integer.toHexString(this.color)==Integer.toHexString(color))
            return true
        return false
    }

    fun isUsed()= count>0

    fun isLimit() = limit>0

    fun getColor() = this.color

    fun setCount(_count: Int){
        this.count = _count
    }
}