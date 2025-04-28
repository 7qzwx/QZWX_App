package com.qzwx.feature_accountbook.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/** 交易表（Transaction） */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"]),
        ForeignKey(entity = LocationTag::class,
            parentColumns = ["id"],
            childColumns = ["locationId"]),
        ForeignKey(entity = Wallet::class, parentColumns = ["id"], childColumns = ["walletId"]),
        ForeignKey(entity = AccountBook::class,
            parentColumns = ["id"],
            childColumns = ["accountBookId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val amount : Double,
    val type : String, // "支出" 或 "收入"
    val categoryId : Long,
    val tagId : Long?,
    val locationId : Long?,
    val walletId : Long,
    val accountBookId : Long,
    val remark : String?,
    val date : Long
)

/** 分类表（Category） */
@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String, // 分类名称
    val type : String, // 分类类型："支出" 或 "收入"
    val parentCategoryId : Long? = null // 父分类ID，null表示是主分类
)

/** 标签表（Tag） */
@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String
)

/** 地点标签表（LocationTag） */
@Entity
data class LocationTag(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String
)

/** 钱包表（Wallet） */
@Entity
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String
)

/** 账本表（AccountBook） */
@Entity
data class AccountBook(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String
)