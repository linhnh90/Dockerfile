package com.styl.pa.modules.dialog.scanNric

import android.app.Activity
import com.styl.pa.entities.classes.ClassInfo
import com.styl.pa.entities.generateToken.PageByClassInfo
import com.styl.pa.modules.base.IBaseContract

interface IScanNricContact {
    interface IView : IBaseContract.IBaseView {
        fun onScanNricSuccess(data: String)

        fun dismissScanDialog()

        fun showErrorMessageAndTitle(message: Int, title: Int)
    }

    interface IPresenter : IBaseContract.IBasePresenter {
        fun startScan(activity: Activity)
        fun stopScan(activity: Activity)
    }

    interface IInteractor : IBaseContract.IBaseInteractor {
        fun getAllClassInfo(data: String, pageIndex: Int?, pageSize: Int?, allClassInfoCallback: IBaseContract.IBaseInteractorOutput<PageByClassInfo>)
    }

    interface IInteractorOutput {

    }

    interface IRouter : IBaseContract.IBaseRouter {
        fun navigationCourseDetailPage(classInfo: ClassInfo?)
    }
}