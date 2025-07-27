import { StyleSheet, Text, View } from "react-native";

export default function NotAuthenticatedScreen() {
  return (
    <View style={styles.container}>
      <Text>Not Authenticated Screen</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "white",
  },
});
