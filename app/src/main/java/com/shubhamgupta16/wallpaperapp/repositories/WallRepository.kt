package com.shubhamgupta16.wallpaperapp.repositories

import com.shubhamgupta16.wallpaperapp.models.roommodels.FavWallModel
import com.shubhamgupta16.wallpaperapp.models.wallpapers.WallModel
import com.shubhamgupta16.wallpaperapp.models.wallpapers.WallpaperPageModel
import com.shubhamgupta16.wallpaperapp.network.ApiResponse
import com.shubhamgupta16.wallpaperapp.network.ApiService
import com.shubhamgupta16.wallpaperapp.network.request.RequestIdModel
import com.shubhamgupta16.wallpaperapp.room.FavWallDao
import retrofit2.Response
import javax.inject.Inject

class WallRepository @Inject constructor(private val apiService: ApiService, private val  favDao: FavWallDao) {


    suspend fun downloadWallpaper(wallId: Int){
        ApiResponse.from { apiService.downloadWallpaper(wallId) }
    }

    suspend fun getWalls(
        page: Int = 1,
        perPage: Int? = null,
        s: String? = null,
        orderBy: String? = null,
        category: String? = null,
        color: String? = null,
    ): ApiResponse<WallpaperPageModel> {
        return ApiResponse.from { apiService.getWalls(page, perPage, s, orderBy, category, color) }
            .apply {
                this.data?.data?.let { filterFavorites(it) }
            }

        /*return try {
            val response = apiService.getWalls(page, perPage, s, orderBy, category, color)
            filterFav(response)
        } catch (e:IOException){
            Log.d("Repo", "error: ${e.message}")
            ApiResponse(800, e.message)
        }*/
    }

    suspend fun getWallsWithIds(
        wallIds: List<Int>,
        page: Int = 1,
        perPage: Int? = null,
    ): ApiResponse<WallpaperPageModel> {
        return ApiResponse.from { apiService.getWallsWithIds(
            RequestIdModel(wallIds),
            page,
            perPage
        ) }.apply {
            this.data?.data?.let { filterFavorites(it) }
//            filterFavorite(this)
        }
    }

    suspend fun getFavoriteWallpapers(
        page: Int = 1,
        perPage: Int? = null
    ): ApiResponse<WallpaperPageModel> {
        val wallIds = favDao.getAllFavorites().map { it.wallId }
        if (wallIds.isEmpty()) return ApiResponse(
            WallpaperPageModel(
                1,
                ArrayList(),
                1,
                1,
                18,
                1,
                0
            )
        )
        return getWallsWithIds(wallIds)
    }

    suspend fun removeFav(wallId: Int) {
        favDao.deleteFav(wallId)
    }

    suspend fun applyFav(wallId: Int) {
        favDao.insertFav(FavWallModel(wallId = wallId))
    }

    /*private suspend fun filterFavorite(response: ApiResponse<WallpaperPageModel>) {
        if (response.data != null) {
            for ((i, wall) in response.data!!.data.withIndex()) {
                if (favDao.isFav(wall.wallId) != null)
                    response.data!!.data[i].isFav = true
            }
        }
    }*/
    /*private suspend fun filterFav(response: Response<WallpaperPageModel>): ApiResponse<WallpaperPageModel> {
        return if (response.isSuccessful) {
            if (response.body() != null) {
                for ((i, wall) in response.body()!!.data.withIndex()) {
                    if (favDao.isFav(wall.wallId) != null)
                        response.body()!!.data[i].isFav = true
                }
                ApiResponse(response.body()!!)
            } else
                ApiResponse(500)
        } else {
            ApiResponse(response.code())
        }
    }*/

    suspend fun filterFavorites(_list: List<WallModel?>) {
        for ((i, wall) in _list.withIndex()) {
            if (wall == null) continue
            _list[i]?.isFav = favDao.isFav(wall.wallId) != null
        }
    }
}