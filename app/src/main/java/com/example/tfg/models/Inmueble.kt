package com.example.tfg.models

import android.os.Parcel
import android.os.Parcelable

data class Inmueble(
    var alquilado: Boolean = false,
    var ciudad: String="",
    var escritura: String="",
    var idInmueble: String="",
    var imagen: String="",
    var nombre: String="",
    var ubicacion: String="",
    var usuario: String="",
    var codigoPostal: String=""
): Parcelable {
    constructor() : this(false, "", "", "", "", "", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (alquilado) 1 else 0)
        parcel.writeString(ciudad)
        parcel.writeString(escritura)
        parcel.writeString(idInmueble)
        parcel.writeString(imagen)
        parcel.writeString(nombre)
        parcel.writeString(ubicacion)
        parcel.writeString(usuario)
        parcel.writeString(codigoPostal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Inmueble> {
        override fun createFromParcel(parcel: Parcel): Inmueble {
            return Inmueble(parcel)
        }

        override fun newArray(size: Int): Array<Inmueble?> {
            return arrayOfNulls(size)
        }
    }
}