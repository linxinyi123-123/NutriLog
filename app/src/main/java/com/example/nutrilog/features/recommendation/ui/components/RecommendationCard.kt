package com.example.nutrilog.features.recommendation.ui.components

// features/recommendation/ui/components/RecommendationCard.kt
@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onDismiss) {
                    Text("稍后再说")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onApply) {
                    Text("立即执行")
                }
            }
        }
    }
}