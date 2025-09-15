@Composable
fun WheelTimeSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val selectedIndex = items.indexOf(selectedItem)
    var currentMiddleItem by remember { mutableStateOf(selectedItem) }
    var isInitialized by remember { mutableStateOf(false) }
    
    // Create infinite list by repeating items multiple times
    val infiniteItems = remember(items) {
        val repeatedItems = items.flatMap { item -> List(100) { item } } // Repeat 100 times for infinite effect
        repeatedItems
    }
    
    // Calculate the center index in the infinite list
    val centerIndex = (infiniteItems.size / 2) + selectedIndex
    
    // Scroll to selected item only once when component is first composed
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0 && !isInitialized) {
            listState.animateScrollToItem(centerIndex)
            isInitialized = true
        }
    }
    
    // Track which item is currently in the middle - only when user scrolls
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (isInitialized) {
            val visibleItemIndex = listState.firstVisibleItemIndex
            val scrollOffset = listState.firstVisibleItemScrollOffset
            val itemHeight = 50.dp.value + 2.dp.value // height + spacing
            
            // Calculate which item is in the center (200dp total height, 100dp from top is center)
            val centerPosition = 100.dp.value
            val currentItemOffset = scrollOffset
            
            // Determine which item is in the center
            val centerIndex = if (centerPosition <= currentItemOffset + itemHeight / 2) {
                visibleItemIndex
            } else {
                visibleItemIndex + 1
            }
            
            if (centerIndex < infiniteItems.size && centerIndex >= 0) {
                val newCenterItem = infiniteItems[centerIndex]
                if (newCenterItem != currentMiddleItem) {
                    currentMiddleItem = newCenterItem
                    onItemSelected(newCenterItem)
                }
            }
        }
    }
    
    Box(
        modifier = modifier
            .height(200.dp)
            .background(
                color = Gray50,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 75.dp), // Center the middle item
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(infiniteItems.size) { index ->
                val item = infiniteItems[index]
                val isSelected = item == currentMiddleItem
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = if (isSelected) 20.sp else 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Blue600 else Gray400, // Fade non-selected items
                        modifier = Modifier
                    )
                }
            }
        }
        
        // Selection indicator lines - gray zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    color = Gray600.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .align(Alignment.Center)
        )
        
        // Top and bottom fade effects
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White)
                    )
                )
                .align(Alignment.BottomCenter)
        )
    }
}