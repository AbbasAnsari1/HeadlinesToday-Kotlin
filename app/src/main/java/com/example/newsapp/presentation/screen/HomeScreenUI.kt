package com.example.newsapp.presentation.screen

import com.example.newsapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.newsapp.data.model.ApiResponse
import com.example.newsapp.presentation.NewsAppViewModel
import com.example.newsapp.presentation.navigation.routes.CategoryScreen

@Composable
fun HomeScreenUI(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NewsAppViewModel
) {
    val scrollState = rememberLazyListState()
    val selectedCategory = remember { mutableStateOf("") }
    val searchTerm = rememberSaveable { mutableStateOf("") }
    val state = viewModel.state.collectAsState()
    val newsCategories = remember {
        mutableStateOf(
            listOf(
                "Business",
                "Entertainment",
                "General",
                "Health",
                "Science",
                "Sports",
                "Technology",
                "World",
                "Politics",
                "Education",
                "Lifestyle",
                "Environment",
                "Crime",
                "Travel",
                "Food",
                "Finance",
                "Automobile",
                "Startups",
                "Fashion",
                "Gaming",
                "Weather"
            )
        )
    }

    if (state.value.loading == true) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (state.value.error.isNullOrBlank().not()) {
        Text(text = state.value.error.toString())
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(value = searchTerm.value,
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                label = { Text(text = "Search") },
                placeholder = { Text(text = "Type, Tap, and Get the News...") },
                onValueChange = {
                    searchTerm.value = it
                }, trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search",
                        modifier = Modifier.clickable(
                            enabled = searchTerm.value.isBlank().not(), onClick = {
                                viewModel.getEverything(q = searchTerm.value)
                            })
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp),
                state = scrollState
            ) {
                items(newsCategories.value) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (it == selectedCategory.value) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .clickable {
                                viewModel.getEverything(q = it)
                                selectedCategory.value = it
                            },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(it, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                }
            }
            val data: ApiResponse? = state.value.data
            if (data?.articles!!.isEmpty()) {
                Text(text = "No Data Found!")
            } else {
                val articles = data.articles
                LazyColumn(modifier = modifier.fillMaxSize()) {
                    items(articles) { article ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 9.dp, end = 9.dp, top = 6.dp, bottom = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                            onClick = {
                                navController.navigate(
                                    CategoryScreen(
                                        author = article.author,
                                        content = article.content,
                                        description = article.description,
                                        publishedAt = article.publishedAt,
                                        id = article.source?.id,
                                        name = article.source?.name,
                                        title = article.title,
                                        url = article.url,
                                        urlToImage = article.urlToImage
                                    )
                                )
                            }
                        ) {
                            Column {
                                Column {
                                    if (article.urlToImage.isNullOrBlank()) {
                                        Image(
                                            painter = painterResource(id = R.drawable.image),
                                            contentDescription = "img not available",
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        AsyncImage(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.Crop,
                                            model = article.urlToImage,
                                            contentDescription = "News Image",
                                            placeholder = painterResource(R.drawable.loading)
                                        )
                                    }
                                    article.title?.let {
                                        Text(
                                            text = it,
                                            modifier = modifier.padding(6.dp),
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.W400,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
