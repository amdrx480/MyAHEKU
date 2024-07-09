package com.dicoding.picodiploma.loginwithanimation.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

object RawQueryHelper {

    fun buildStocksQuery(
        sort: String? = null,
        search: String? = null,
        order: String? = "ASC", // default order
        categoryName: List<String>? = null,
        unitName: List<String>? = null,
        sellingPriceMin: Int? = null,
        sellingPriceMax: Int? = null
    ): SupportSQLiteQuery {
        val queryBuilder = StringBuilder("SELECT * FROM stocks")
        val args = mutableListOf<Any>()

        var isFirstCondition = true

        // Adding WHERE clause if any filtering condition is provided
        if (!search.isNullOrBlank() || !categoryName.isNullOrEmpty() || !unitName.isNullOrEmpty()) {
            queryBuilder.append(" WHERE ")

            // Adding main filter condition
            if (!search.isNullOrBlank()) {
                queryBuilder.append("(stock_name LIKE ? OR stock_code LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
                val searchArg = "%$search%"
                args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
                isFirstCondition = false
            }

            // Adding category_name filter
            if (!categoryName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("category_name IN (${categoryName.joinToString { "?" }})")
                args.addAll(categoryName)
                isFirstCondition = false
            }

            // Adding unit_name filter
            if (!unitName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("unit_name IN (${unitName.joinToString { "?" }})")
                args.addAll(unitName)
                isFirstCondition = false
            }

            // Adding selling_price range filter
            if (sellingPriceMin != null && sellingPriceMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price BETWEEN ? AND ?")
                args.addAll(listOf(sellingPriceMin, sellingPriceMax))
                isFirstCondition = false
            } else if (sellingPriceMin != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price >= ?")
                args.add(sellingPriceMin)
                isFirstCondition = false
            } else if (sellingPriceMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price <= ?")
                args.add(sellingPriceMax)
                isFirstCondition = false
            }
        }

        // Adding search query condition
        if (!search.isNullOrBlank()) {
            if (!isFirstCondition) {
                queryBuilder.append(" AND ")
            } else {
                queryBuilder.append(" WHERE ")
            }
            queryBuilder.append(" (stock_name LIKE ? OR stock_code LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
            val searchArg = "%$search%"
            args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
        }

        // Adding sorting order
        when (sort) {
            "selling_price" -> queryBuilder.append(" ORDER BY selling_price $order")
            else -> queryBuilder.append(" ORDER BY stock_name $order")
        }

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }

    fun buildPurchasesQuery(
        sort: String? = null,
        search: String? = null,
        order: String? = "ASC", // default order
        categoryName: List<String>? = null,
        unitName: List<String>? = null,
        sellingPriceMin: Int? = null,
        sellingPriceMax: Int? = null
    ): SupportSQLiteQuery {
        val queryBuilder = StringBuilder("SELECT * FROM purchases")
        val args = mutableListOf<Any>()

        var isFirstCondition = true

        // Adding WHERE clause if any filtering condition is provided
        if (!search.isNullOrBlank() || !categoryName.isNullOrEmpty() || !unitName.isNullOrEmpty()) {
            queryBuilder.append(" WHERE ")

            // Adding main filter condition
            if (!search.isNullOrBlank()) {
                queryBuilder.append("(stock_name LIKE ? OR stock_code LIKE ? OR vendor_name LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
                val searchArg = "%$search%"
                args.addAll(listOf(searchArg, searchArg, searchArg, searchArg, searchArg))
                isFirstCondition = false
            }

            // Adding category_name filter
            if (!categoryName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("category_name IN (${categoryName.joinToString { "?" }})")
                args.addAll(categoryName)
                isFirstCondition = false
            }

            // Adding unit_name filter
            if (!unitName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("unit_name IN (${unitName.joinToString { "?" }})")
                args.addAll(unitName)
                isFirstCondition = false
            }

            // Adding selling_price range filter
            if (sellingPriceMin != null && sellingPriceMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price BETWEEN ? AND ?")
                args.addAll(listOf(sellingPriceMin, sellingPriceMax))
                isFirstCondition = false
            } else if (sellingPriceMin != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price >= ?")
                args.add(sellingPriceMin)
                isFirstCondition = false
            } else if (sellingPriceMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("selling_price <= ?")
                args.add(sellingPriceMax)
                isFirstCondition = false
            }
        }

        // Adding search query condition
        if (!search.isNullOrBlank()) {
            if (!isFirstCondition) {
                queryBuilder.append(" AND ")
            } else {
                queryBuilder.append(" WHERE ")
            }
            queryBuilder.append(" (stock_name LIKE ? OR stock_code LIKE ? OR vendor_name LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
            val searchArg = "%$search%"
            args.addAll(listOf(searchArg, searchArg, searchArg, searchArg, searchArg))
        }

        // Adding sorting order
        when (sort) {
            "selling_price" -> queryBuilder.append(" ORDER BY selling_price $order")
//            "vendor_name" -> queryBuilder.append(" ORDER BY vendor_name $order")
            else -> queryBuilder.append(" ORDER BY rowid $order") // default order by rowid
        }

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }

    fun buildItemTransactionsQuery(
        sort: String? = null,
        search: String? = null,
        order: String? = "ASC", // default order
        categoryName: List<String>? = null,
        unitName: List<String>? = null,
        subTotalMin: Int? = null,
        subTotalMax: Int? = null,
    ): SupportSQLiteQuery {
        val queryBuilder = StringBuilder("SELECT * FROM item_transactions")
        val args = mutableListOf<Any>()

        var isFirstCondition = true

        // Adding WHERE clause if any filtering condition is provided
        if (!search.isNullOrBlank() || !categoryName.isNullOrEmpty() || !unitName.isNullOrEmpty()) {
            queryBuilder.append(" WHERE ")

            // Adding main filter condition
            if (!search.isNullOrBlank()) {
                queryBuilder.append("(stock_name LIKE ? OR stock_code LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
                val searchArg = "%$search%"
                args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
                isFirstCondition = false
            }

            // Adding category_name filter
            if (!categoryName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("category_name IN (${categoryName.joinToString { "?" }})")
                args.addAll(categoryName)
                isFirstCondition = false
            }

            // Adding unit_name filter
            if (!unitName.isNullOrEmpty()) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("unit_name IN (${unitName.joinToString { "?" }})")
                args.addAll(unitName)
                isFirstCondition = false
            }

            // Adding sub_total range filter
            if (subTotalMin != null && subTotalMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("sub_total BETWEEN ? AND ?")
                args.addAll(listOf(subTotalMin, subTotalMax))
                isFirstCondition = false
            } else if (subTotalMin != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("sub_total >= ?")
                args.add(subTotalMin)
                isFirstCondition = false
            } else if (subTotalMax != null) {
                if (!isFirstCondition) {
                    queryBuilder.append(" AND ")
                }
                queryBuilder.append("sub_total <= ?")
                args.add(subTotalMax)
                isFirstCondition = false
            }
        }

        // Adding search query condition
        if (!search.isNullOrBlank()) {
            if (!isFirstCondition) {
                queryBuilder.append(" AND ")
            } else {
                queryBuilder.append(" WHERE ")
            }
            queryBuilder.append(" (stock_name LIKE ? OR stock_code LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
            val searchArg = "%$search%"
            args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
        }

        // Adding sorting order
        when (sort) {
            "sub_total" -> queryBuilder.append(" ORDER BY sub_total $order")
            else -> queryBuilder.append(" ORDER BY rowid $order") // default order by rowid
        }

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }

}


//    fun buildPurchasesQuery(
//        sort: String? = null,
//        search: String? = null,
//        order: String? = "ASC", // default order
//        categoryName: List<String>? = null,
//        unitName: List<String>? = null,
//        sellingPriceMin: Int? = null,
//        sellingPriceMax: Int? = null
//    ): SupportSQLiteQuery {
//        val queryBuilder = StringBuilder("SELECT * FROM purchases")
//        val args = mutableListOf<Any>()
//
//        var isFirstCondition = true
//
//        // Adding WHERE clause if any filtering condition is provided
//        if (!search.isNullOrBlank() || !categoryName.isNullOrEmpty() || !unitName.isNullOrEmpty()) {
//            queryBuilder.append(" WHERE ")
//
//            // Adding main filter condition
//            if (!search.isNullOrBlank()) {
//                queryBuilder.append("(stock_name LIKE ? OR stock_code LIKE ? OR vendor_name LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
//                val searchArg = "%$search%"
//                args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
//                isFirstCondition = false
//            }
//
//            // Adding category_name filter
//            if (!categoryName.isNullOrEmpty()) {
//                if (!isFirstCondition) {
//                    queryBuilder.append(" AND ")
//                }
//                queryBuilder.append("category_name IN (${categoryName.joinToString { "?" }})")
//                args.addAll(categoryName)
//                isFirstCondition = false
//            }
//
//            // Adding unit_name filter
//            if (!unitName.isNullOrEmpty()) {
//                if (!isFirstCondition) {
//                    queryBuilder.append(" AND ")
//                }
//                queryBuilder.append("unit_name IN (${unitName.joinToString { "?" }})")
//                args.addAll(unitName)
//                isFirstCondition = false
//            }
//
//            // Adding selling_price range filter
//            if (sellingPriceMin != null && sellingPriceMax != null) {
//                if (!isFirstCondition) {
//                    queryBuilder.append(" AND ")
//                }
//                queryBuilder.append("selling_price BETWEEN ? AND ?")
//                args.addAll(listOf(sellingPriceMin, sellingPriceMax))
//                isFirstCondition = false
//            } else if (sellingPriceMin != null) {
//                if (!isFirstCondition) {
//                    queryBuilder.append(" AND ")
//                }
//                queryBuilder.append("selling_price >= ?")
//                args.add(sellingPriceMin)
//                isFirstCondition = false
//            } else if (sellingPriceMax != null) {
//                if (!isFirstCondition) {
//                    queryBuilder.append(" AND ")
//                }
//                queryBuilder.append("selling_price <= ?")
//                args.add(sellingPriceMax)
//                isFirstCondition = false
//            }
//        }
//
//        // Adding search query condition
//        if (!search.isNullOrBlank()) {
//            if (!isFirstCondition) {
//                queryBuilder.append(" AND ")
//            } else {
//                queryBuilder.append(" WHERE ")
//            }
//            queryBuilder.append(" (stock_name LIKE ? OR stock_code LIKE ? OR vendor_name LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
//            val searchArg = "%$search%"
//            args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
//        }
//
//        // Adding sorting order
//        when (sort) {
//            "selling_price" -> queryBuilder.append(" ORDER BY selling_price $order")
//            else -> queryBuilder.append(" ORDER BY stock_name $order")
//        }
//
//        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
//    }


//object RawQueryHelper {
//
//    fun buildStocksQuery(
//        filter: String? = null,
//        sort: String? = null,
//        query: String? = null,
//        order: String? = "ASC", // default order
//    ): SupportSQLiteQuery {
//        val queryBuilder = StringBuilder("SELECT * FROM stocks")
//        val args = mutableListOf<Any>()
//
//        if (!filter.isNullOrBlank()) {
//            queryBuilder.append(" WHERE category_name LIKE ?")
//            args.add("%$filter%")
//        }
//
//        if (!query.isNullOrBlank()) {
//            if (args.isEmpty()) {
//                queryBuilder.append(" WHERE")
//            } else {
//                queryBuilder.append(" AND")
//            }
//            queryBuilder.append(" (stock_name LIKE ? OR stock_code LIKE ? OR category_name LIKE ? OR unit_name LIKE ?)")
//            val searchArg = "%$query%"
//            args.addAll(listOf(searchArg, searchArg, searchArg, searchArg))
//        }
//
//        when (sort) {
//            "selling_price" -> queryBuilder.append(" ORDER BY selling_price $order")
//            else -> queryBuilder.append(" ORDER BY stock_name $order")
//        }
//
//        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
//    }
//}