package com.dicoding.picodiploma.loginwithanimation.ui.category

//class CategoryViewModel: ViewModel() {
//    private val mIsLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = mIsLoading
//
//    fun getCategory(
//        user: UserModel,
//        listCategoryItem: ListCategoryItem,
//        callback: helper.ApiCallBackString
//
//    ) {
//        mIsLoading.value = true
//        val service = ApiConfig().ApiService().getCategories("Bearer ${user.token}", listCategoryItem)
//        service.enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(
//                call: Call<ApiResponse>,
//                response: Response<ApiResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null && !responseBody.error) {
//                        callback.onResponse(response.body() != null, SUCCESS)
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//
//                    // get message error
//                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
//                    val message = jsonObject.getString("message")
//                    callback.onResponse(false, message)
//                }
//            }
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                mIsLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message}")
//                callback.onResponse(false, t.message.toString())
//            }
//        })
//
//    }
//
//    companion object {
//        private const val TAG = "AddStoryViewModel"
//        private const val SUCCESS = "success"
//    }
//}