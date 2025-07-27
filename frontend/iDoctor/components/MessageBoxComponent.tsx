import { View, StyleSheet, Text } from "react-native";

export default function MessageBoxComponent({
  text,
  isSentMessage,
}: {
  text: string;
  isSentMessage: boolean;
}) {
  return (
    <View
      style={[
        styles.bubble,
        isSentMessage ? styles.sentBubble : styles.receivedBubble,
      ]}
    >
      <Text
        style={[
          styles.text,
          isSentMessage ? styles.sentText : styles.receivedText,
        ]}
      >
        {text}
      </Text>
      {isSentMessage ? (
        <>
          <View style={styles.rightArrow} />
          <View style={styles.rightArrowOverlap} />
        </>
      ) : (
        <>
          <View style={styles.leftArrow} />
          <View style={styles.leftArrowOverlap} />
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  bubble: {
    padding: 10,
    borderRadius: 20,
    marginTop: 15,
    maxWidth: "50%",
    marginBottom: 10,
  },
  sentBubble: {
    backgroundColor: "#0078fe",
    alignSelf: "flex-end",
    marginRight: "5%",
  },
  receivedBubble: {
    backgroundColor: "#dedede",
    alignSelf: "flex-start",
    marginLeft: "5%",
  },
  text: {
    fontSize: 16,
  },
  sentText: {
    color: "#fff",
  },
  receivedText: {
    color: "#000",
  },
  rightArrow: {
    position: "absolute",
    backgroundColor: "#0078fe",
    width: 20,
    height: 25,
    bottom: 0,
    borderBottomLeftRadius: 25,
    right: -10,
  },
  rightArrowOverlap: {
    position: "absolute",
    backgroundColor: "white",
    width: 20,
    height: 35,
    bottom: -6,
    borderBottomLeftRadius: 18,
    right: -20,
  },
  leftArrow: {
    position: "absolute",
    backgroundColor: "#dedede",
    width: 20,
    height: 25,
    bottom: 0,
    borderBottomRightRadius: 25,
    left: -10,
  },
  leftArrowOverlap: {
    position: "absolute",
    backgroundColor: "white",
    width: 20,
    height: 35,
    bottom: -6,
    borderBottomRightRadius: 18,
    left: -20,
  },
});
