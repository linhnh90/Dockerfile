package com.styl.pa.entities.classes

import android.os.Parcel
import android.os.Parcelable
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName
import com.styl.pa.entities.generateToken.ResourceFeeList
import com.styl.pa.entities.product.Product
import com.styl.pa.services.Config

/**
 * Created by Ngatran on 09/10/2018.
 */
class ClassInfo : Parcelable {
    @SerializedName("classId")
//    @Expose
    private var classId: String? = null
    @SerializedName("classCode")
//    @Expose
    private var classCode: String? = null
    @SerializedName("classTitle")
//    @Expose
    private var classTitle: String? = null
    @SerializedName("classDescription")
//    @Expose
    private var classDescription: String? = null
    @SerializedName("imageURL")
//    @Expose
    private var imageURL: String? = null
    @SerializedName("courseId")
//    @Expose
    private var courseId: String? = null
    @SerializedName("courseCode")
//    @Expose
    private var courseCode: String? = null
    @SerializedName("outletId")
//    @Expose
    private var outletId: String? = null
    @SerializedName("outletName")
//    @Expose
    private var outletName: String? = null
    @SerializedName("outletEmail")
//    @Expose
    private var outletEmail: String? = null
    @SerializedName("outletPhone")
//    @Expose
    private var outletPhone: String? = null
    @SerializedName("language")
//    @Expose
    private var language: String? = null
    @SerializedName("vacancies")
//    @Expose
    private var vacancies: String? = null
    @SerializedName(value = "closingDate", alternate = ["onlineRegistrationClosingDate"])
//    @Expose
    private var closingDate: String? = null
    @SerializedName("startDate")
//    @Expose
    private var startDate: String? = null
    @SerializedName("endDate")
//    @Expose
    private var endDate: String? = null
    @SerializedName("status")
//    @Expose
    private var status: String? = null
    @SerializedName("isPublic")
//    @Expose
    private var isPublic: Boolean? = null
    @SerializedName("canRegisterOnline")
//    @Expose
    private var canRegisterOnline: Boolean? = null
    @SerializedName("indemnityRequired")
//    @Expose
    private var indemnityRequired: Boolean? = null
    @SerializedName("indemnityDescription")
//    @Expose
    private var indemnityDescription: String? = null
    @SerializedName("targetCustomerSegments")
//    @Expose
    private var targetCustomerSegments: List<String>? = null
    @SerializedName("classTrainers")
//    @Expose
    private var classTrainers: List<ClassTrainer>? = null
    @SerializedName("classPrerequisites")
//    @Expose
    private var classPrerequisites: List<ClassPrerequisite>? = null
    @SerializedName("classSessions")
//    @Expose
    private var classSessions: List<ClassSession>? = null
    @SerializedName("totalSessions")
    var totalSessions: Int? = null
    @SerializedName("classFees")
//    @Expose
    private var classFees: List<ClassFee>? = null

    @SerializedName("maxvacancy")
//    @Expose
    private var maxvacancy: Int? = 0

    @SerializedName("outletTypeName")
    var outletTypeName: String? = null

    @SerializedName("indemnityUrl")
    var indemnityUrl: String? = null

    @SerializedName("isSkillsFutureEnabled")
    var isSkillsFutureEnabled: Boolean? = false

    @SerializedName("classRequirementsAndRemarks")
    var classRequirements: String? = null

    @SerializedName("classRemarks")
    var classRemarks: String? = null

    @SerializedName("sku", alternate = ["Sku"])
    var sku: String? = null

    @SerializedName("CrmresourceId", alternate = ["crmResourceId", "crmresourceId"])
    var crmResourceId: String? = null

    @SerializedName("minPrice")
    var minPrice: Float? = null

    @SerializedName("maxPrice")
    var maxPrice: Float? = null

    @SerializedName("materialFees")
    var materialFees: List<ResourceFeeList>? = null

    constructor(parcel: Parcel) : this() {
        classId = parcel.readString()
        classCode = parcel.readString()
        classTitle = parcel.readString()
        imageURL = parcel.readString()
        courseId = parcel.readString()
        courseCode = parcel.readString()
        outletId = parcel.readString()
        classDescription = parcel.readString()
        outletName = parcel.readString()
        outletEmail = parcel.readString()
        outletPhone = parcel.readString()
        language = parcel.readString()
        vacancies = parcel.readString()
        closingDate = parcel.readString()
        startDate = parcel.readString()
        endDate = parcel.readString()
        status = parcel.readString()
        canRegisterOnline = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityRequired = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        indemnityDescription = parcel.readString()
        targetCustomerSegments = parcel.createStringArrayList()
        classTrainers = parcel.createTypedArrayList(ClassTrainer)
        classPrerequisites = parcel.createTypedArrayList(ClassPrerequisite)
        classSessions = parcel.createTypedArrayList(ClassSession)
        totalSessions = parcel.readValue(Int::class.java.classLoader) as? Int
        classFees = parcel.createTypedArrayList(ClassFee)
        classDescription = parcel.readString()
        maxvacancy = parcel.readValue(Int::class.java.classLoader) as? Int
        outletTypeName = parcel.readString()
        indemnityUrl = parcel.readString()
        isSkillsFutureEnabled = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        classRequirements = parcel.readString()
        classRemarks = parcel.readString()
        sku = parcel.readString()
        crmResourceId = parcel.readString()
        minPrice = parcel.readValue(Float::class.java.classLoader) as? Float
        maxPrice = parcel.readValue(Float::class.java.classLoader) as? Float
    }

    fun getClassId(): String? {
        return classId
    }

    fun setClassId(classId: String) {
        this.classId = classId
    }

    fun getClassCode(): String? {
        return classCode
    }

    fun setClassCode(classCode: String) {
        this.classCode = classCode
    }

    fun getClassTitle(): String? {
        return classTitle
    }

    fun getDecodedTitle(): Spanned {
        return HtmlCompat.fromHtml(classTitle ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun setClassTitle(classTitle: String) {
        this.classTitle = classTitle
    }

    fun getImageURL(): String? {
        if (true == imageURL?.contains(Config.BASE_IMAGE_URL)) {
            return imageURL
        }
        return Config.BASE_IMAGE_URL + imageURL
    }

    fun setImageURL(imageURL: String) {
        this.imageURL = imageURL
    }

    fun getCourseId(): String? {
        return courseId
    }

    fun setCourseId(courseId: String) {
        this.courseId = courseId
    }

    fun getCourseCode(): String? {
        return courseCode
    }

    fun setCourseCode(courseCode: String) {
        this.courseCode = courseCode
    }

    fun getOutletId(): String? {
        return outletId
    }

    fun setOutletId(outletId: String) {
        this.outletId = outletId
    }

    fun getClassDescription(): String? {
        return classDescription
    }

    fun getDecodedDescription(): Spanned {
        return HtmlCompat.fromHtml(classDescription ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun setClassDescription(classDescription: String) {
        this.classDescription = classDescription
    }

    fun getOutletName(): String? {
        return outletName
    }

    fun setOutletName(outletName: String) {
        this.outletName = outletName
    }

    fun getOutletEmail(): String? {
        return outletEmail
    }

    fun setOutletEmail(outletEmail: String) {
        this.outletEmail = outletEmail
    }

    fun getOutletPhone(): String? {
        return outletPhone
    }

    fun setOutletPhone(outletPhone: String) {
        this.outletPhone = outletPhone
    }

    fun getLanguage(): String? {
        return language
    }

    fun setLanguage(language: String) {
        this.language = language
    }

    fun getVacancies(): String? {
        return vacancies
    }

    fun setVacancies(vacancies: String?) {
        this.vacancies = vacancies
    }

    fun getClosingDate(): String? {
        return closingDate
    }

    fun setClosingDate(closingDate: String) {
        this.closingDate = closingDate
    }

    fun getStartDate(): String? {
        return startDate
    }

    fun setStartDate(startDate: String) {
        this.startDate = startDate
    }

    fun getEndDate(): String? {
        return endDate
    }

    fun setEndDate(endDate: String) {
        this.endDate = endDate
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun isPublic(): Boolean? {
        return isPublic
    }

    fun getCanRegisterOnline(): Boolean? {
        return canRegisterOnline
    }

    fun setCanRegisterOnline(canRegisterOnline: Boolean?) {
        this.canRegisterOnline = canRegisterOnline
    }

    fun getIndemnityRequired(): Boolean? {
        return indemnityRequired
    }

    fun setIndemnityRequired(indemnityRequired: Boolean?) {
        this.indemnityRequired = indemnityRequired
    }

    fun getIndemnityDescription(): String? {
        return indemnityDescription
    }

    fun setIndemnityDescription(indemnityDescription: String) {
        this.indemnityDescription = indemnityDescription
    }

    fun getTargetCustomerSegments(): List<String>? {
        return targetCustomerSegments
    }

    fun setTargetCustomerSegments(targetCustomerSegments: List<String>) {
        this.targetCustomerSegments = targetCustomerSegments
    }

    fun getClassTrainers(): List<ClassTrainer>? {
        return classTrainers
    }

    fun setClassTrainers(classTrainers: List<ClassTrainer>) {
        this.classTrainers = classTrainers
    }

    fun getClassPrerequisites(): List<ClassPrerequisite>? {
        return classPrerequisites
    }

    fun setClassPrerequisites(classPrerequisites: List<ClassPrerequisite>) {
        this.classPrerequisites = classPrerequisites
    }

    fun getClassSessions(): List<ClassSession>? {
        return classSessions
    }

    fun setClassSessions(classSessions: List<ClassSession>) {
        this.classSessions = classSessions
    }

    fun getClassFees(): List<ClassFee>? {
        return classFees
    }

    fun setClassFees(classFees: List<ClassFee>) {
        this.classFees = classFees
    }

    fun getMaxVacancy(): Int? {
        return maxvacancy
    }

    fun setMaxVacancy(maxVacancy: Int?) {
        this.maxvacancy = maxVacancy
    }

    fun getPriceRange(): FloatArray {
        val result = FloatArray(2)
        result[0] = this.minPrice ?: 0f
        result[1] = this.maxPrice ?: 0f
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(classId)
        parcel.writeString(classCode)
        parcel.writeString(classTitle)
        parcel.writeString(imageURL)
        parcel.writeString(courseId)
        parcel.writeString(courseCode)
        parcel.writeString(outletId)
        parcel.writeString(classDescription)
        parcel.writeString(outletName)
        parcel.writeString(outletEmail)
        parcel.writeString(outletPhone)
        parcel.writeString(language)
        parcel.writeString(vacancies)
        parcel.writeString(closingDate)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(status)
        parcel.writeValue(canRegisterOnline)
        parcel.writeValue(indemnityRequired)
        parcel.writeString(indemnityDescription)
        parcel.writeStringList(targetCustomerSegments)
        parcel.writeTypedList(classTrainers)
        parcel.writeTypedList(classPrerequisites)
        parcel.writeTypedList(classSessions)
        parcel.writeValue(totalSessions)
        parcel.writeTypedList(classFees)
        parcel.writeString(classDescription)
        parcel.writeValue(maxvacancy)
        parcel.writeString(outletTypeName)
        parcel.writeString(indemnityUrl)
        parcel.writeString(sku)
        parcel.writeString(crmResourceId)
        parcel.writeValue(minPrice)
        parcel.writeValue(maxPrice)
        parcel.writeString(classRemarks)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ClassInfo(classId=$classId, classCode=$classCode, classTitle=$classTitle, classDescription=$classDescription, imageURL=$imageURL, courseId=$courseId, courseCode=$courseCode, outletId=$outletId, outletName=$outletName, outletEmail=$outletEmail, outletPhone=$outletPhone, language=$language, vacancies=$vacancies, closingDate=$closingDate, startDate=$startDate, endDate=$endDate, status=$status, isPublic=$isPublic, canRegisterOnline=$canRegisterOnline, indemnityRequired=$indemnityRequired, indemnityDescription=$indemnityDescription, targetCustomerSegments=$targetCustomerSegments, classTrainers=$classTrainers, classPrerequisites=$classPrerequisites, classSessions=$classSessions, totalSessions=$totalSessions, classFees=$classFees, maxvacancy=$maxvacancy, outletTypeName=$outletTypeName, indemnityUrl=$indemnityUrl)"
    }

    companion object CREATOR : Parcelable.Creator<ClassInfo> {
        override fun createFromParcel(parcel: Parcel): ClassInfo {
            return ClassInfo(parcel)
        }

        override fun newArray(size: Int): Array<ClassInfo?> {
            return arrayOfNulls(size)
        }
    }

    constructor()

    constructor(productInfo: Product) {
        this.classId = productInfo.itemId
        this.classCode = productInfo.productCode
        this.classFees = productInfo.classEventFees
        this.outletId = productInfo.outletId
        this.outletName = productInfo.outletName
        this.outletEmail = productInfo.outletEmail
        this.outletPhone = productInfo.outletPhone
        this.classTitle = productInfo.title
        this.classDescription = productInfo.description
        this.imageURL = productInfo.imageUrl
        this.indemnityRequired = productInfo.indemnityRequired
        this.indemnityDescription = productInfo.indemnityDescription
        this.indemnityUrl = productInfo.indemnityUrl
        this.classPrerequisites = productInfo.classPrerequisites
        this.startDate = productInfo.dateFrom
        this.endDate = productInfo.dateTo
        this.closingDate = productInfo.onlineRegistrationClosingDate
        this.vacancies = productInfo.vacancies.toString()
        this.status = productInfo.status
        this.maxvacancy = productInfo.maxvacancy
        this.language = productInfo.language
        this.totalSessions = productInfo.totalSessions
        this.classTrainers = productInfo.classTrainers
        this.classSessions = productInfo.classSessions
        this.isPublic = productInfo.isPublic
        this.canRegisterOnline = productInfo.canRegisterOnline
        this.targetCustomerSegments = productInfo.targetCustomerSegments
        this.isSkillsFutureEnabled = productInfo.isSkillsFutureEnabled
        this.sku = productInfo.sku
        this.crmResourceId = productInfo.crmResourceId
        this.minPrice = productInfo.minPrice
        this.maxPrice = productInfo.maxPrice
        this.classRemarks = productInfo.classRemarks
        this.materialFees = productInfo.materialFees
    }


}