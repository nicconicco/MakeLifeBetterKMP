package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.components.ProductCard
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory
import com.carlosnicolaugalves.makelifebetter.store.ProductsResult
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    viewModel: SharedStoreViewModel,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val productsState by viewModel.productsState.collectAsState()
    val cart by viewModel.cart.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Loja",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (cart.totalItems > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ) {
                                        Text(
                                            text = cart.totalItems.toString(),
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Carrinho"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category chips
            if (categories.isNotEmpty()) {
                CategoryChipsRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        viewModel.selectCategory(category)
                    }
                )
            }

            // Products grid
            when (productsState) {
                is ProductsResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ProductsResult.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (productsState as ProductsResult.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum produto encontrado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = onProductClick,
                                    onAddToCart = { viewModel.addToCart(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChipsRow(
    categories: List<ProductCategory>,
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "Todos" chip
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Todos") },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // Category chips
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory?.id == category.id,
                onClick = { onCategorySelected(category) },
                label = { Text(category.nome) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
