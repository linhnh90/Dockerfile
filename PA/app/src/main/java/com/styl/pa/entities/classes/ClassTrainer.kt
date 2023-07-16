package com.styl.pa.entities.classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ClassTrainer() :Parcelable {
    @SerializedName("trainerId")
//    @Expose
    private var trainerId: String? = null
    @SerializedName("trainerName")
//    @Expose
    private var trainerName: String? = null

    constructor(parcel: Parcel) : this() {
        trainerId = parcel.readString()
        trainerName = parcel.readString()
    }

    fun getTrainerId(): String? {
        return trainerId
    }

    fun setTrainerId(trainerId: String) {
        this.trainerId = trainerId
    }

    fun getTrainerName(): String? {
        return trainerName
    }

    fun setTrainerName(trainerName: String) {
        this.trainerName = trainerName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trainerId)
        parcel.writeString(trainerName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassTrainer> {
        override fun createFromParcel(parcel: Parcel): ClassTrainer {
            return ClassTrainer(parcel)
        }

        override fun newArray(size: Int): Array<ClassTrainer?> {
            return arrayOfNulls(size)
        }
    }
}