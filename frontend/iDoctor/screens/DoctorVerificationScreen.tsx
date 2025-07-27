import {
  Button,
  Modal,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";
import ScreenTitleComponent from "../components/ScreenTitleComponent";
import Ionicons from "@expo/vector-icons/Ionicons";
import { useState } from "react";
import * as DocumentPicker from "expo-document-picker";
import {
  NavigationProp,
  RouteProp,
  useNavigation,
  useRoute,
} from "@react-navigation/native";
import { RootStackParamList } from "../App";
import FontAwesome6 from "@expo/vector-icons/FontAwesome6";
import { API_URL } from "../util/requests";

type DoctorVerificationRouteProp = RouteProp<
  RootStackParamList,
  "DoctorVerification"
>;

type Document = {
  uri: string;
  name: string;
  type: string | undefined;
};

export default function DoctorVerificationScreen() {
  const [uploaded, setUploaded] = useState<boolean>(false);
  const route = useRoute<DoctorVerificationRouteProp>();
  const [documents, setDocuments] = useState<Document[]>([]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [infoModalVisible, setInfoModalVisible] = useState<boolean>(false);
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  async function handleUpload(): Promise<void> {
    setUploaded(true);

    try {
      const docRes = await DocumentPicker.getDocumentAsync({
        type: "application/pdf",
        multiple: true,
      });

      if (!docRes.assets) {
        return;
      }

      docRes.assets.forEach((file) => {
        setDocuments((prev) => {
          const isDuplicate = prev.some(
            (doc) => doc.name === file.name && doc.type === file.mimeType
          );

          if (isDuplicate) {
            return prev;
          }

          return [
            ...prev,
            { uri: file.uri, name: file.name, type: file.mimeType },
          ];
        });
      });
    } catch (error) {}
  }

  async function uploadFiles(documents: Document[]): Promise<void> {
    const formData = new FormData();

    documents.forEach((file) => {
      formData.append("files", {
        uri: file.uri,
        name: file.name,
        type: file.type,
      } as any);
    });

    try {
      const response = await fetch(
        `${API_URL}/api/document/${route.params.username}/upload`,
        {
          method: "POST",
          headers: {
            "Content-Type": "multipart/form-data",
          },
          body: formData,
        }
      );

      if (response.ok) {
        const data = await response.json();
      } else {
        console.error("Failed to upload file:", response.status);
      }
    } catch (error) {
      console.error("Error while uploading file:", error);
    }
  }

  function deleteDocument(name: string) {
    setDocuments((prev) => {
      const copy = prev.filter((document) => document.name !== name);
      if (copy.length === 0) {
        setUploaded(false);
      }
      return copy;
    });
  }

  return (
    <View style={styles.container}>
      <ScreenTitleComponent
        isMainTitle={false}
        subtitle="Verification Process"
      />

      <Modal animationType="slide" transparent={true} visible={modalVisible}>
        <View style={styles.modelContainer}>
          <View style={styles.modelView}>
            <View
              style={{
                flexDirection: "row",
                justifyContent: "flex-start",
                alignItems: "center",
                gap: 15,
                width: "100%",
              }}
            >
              <FontAwesome6 name="hand-point-right" size={24} color="black" />
              <Text style={styles.neededDocument}>
                A valid medical license or certification
              </Text>
            </View>

            <View
              style={{
                flexDirection: "row",
                justifyContent: "flex-start",
                alignItems: "center",
                gap: 15,
                width: "100%",
              }}
            >
              <FontAwesome6 name="hand-point-right" size={24} color="black" />
              <Text style={styles.neededDocument}>
                A practice registration certificate
              </Text>
            </View>

            <View
              style={{
                flexDirection: "row",
                justifyContent: "flex-start",
                alignItems: "center",
                gap: 15,
                width: "100%",
              }}
            >
              <FontAwesome6 name="hand-point-right" size={24} color="black" />
              <Text style={styles.neededDocument}>
                A government-issued identification document
              </Text>
            </View>

            <View style={{ marginTop: 10 }}>
              <Button title="OK" onPress={() => setModalVisible(false)} />
            </View>
          </View>
        </View>
      </Modal>

      <Modal
        animationType="slide"
        transparent={true}
        visible={infoModalVisible}
      >
        <View style={styles.modelContainer}>
          <View style={styles.infoModelView}>
            <View>
              <Text style={{ textAlign: "center" }}>
                Your verification process has just started. This process might
                take a while as each document is carefully reviewed by our
                verification team. It could take up to several business days for
                approval. You will be notified via email once the verification
                is complete. Until then, you will not be able to sign in with
                this account.
              </Text>

              <Text style={{ marginTop: 10, textAlign: "center" }}>
                Thank you for your understanding.
              </Text>
            </View>

            <View style={{ marginTop: 10 }}>
              <Button
                title="OK"
                onPress={() => {
                  setInfoModalVisible(false);
                  navigation.navigate("Main");
                }}
              />
            </View>
          </View>
        </View>
      </Modal>

      <View style={styles.uploadContainer}>
        <Button
          title="Which documents do I need?"
          onPress={() => setModalVisible(true)}
        />
        <View style={styles.upload}>
          {uploaded ? (
            <ScrollView
              style={{ width: "80%", marginTop: 10, marginBottom: 10 }}
              contentContainerStyle={{
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {documents.map((item) => {
                return (
                  <View
                    key={item.name}
                    style={{
                      flexDirection: "row",
                      justifyContent: "center",
                      alignItems: "center",
                      backgroundColor: "#cfd1b6",
                      marginBottom: 10,
                      height: 40,
                      borderRadius: 5,
                      padding: 5,
                    }}
                  >
                    <Text>{item.name}</Text>
                    <Pressable onPress={() => deleteDocument(item.name)}>
                      <Ionicons name="close" size={24} color="black" />
                    </Pressable>
                  </View>
                );
              })}
            </ScrollView>
          ) : (
            <Pressable
              style={({ pressed }) =>
                pressed
                  ? [styles.pressed, { alignItems: "center" }]
                  : { alignItems: "center" }
              }
              onPress={handleUpload}
            >
              <Ionicons name="document-text-outline" size={50} color="black" />
              <Text style={{ textDecorationLine: "underline" }}>
                Select documents
              </Text>
            </Pressable>
          )}
        </View>

        {uploaded && <Button title="Select Documents" onPress={handleUpload} />}
      </View>
      <Pressable
        style={({ pressed }) =>
          pressed ? [styles.pressed, styles.signUpBtn] : styles.signUpBtn
        }
        onPress={() => {
          uploadFiles(documents);
          setInfoModalVisible(true);
        }}
      >
        <Text style={{ color: "white", fontWeight: "bold" }}>DONE</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
  },
  uploadContainer: {
    marginTop: 50,
    height: "50%",
  },
  upload: {
    marginTop: 10,
    justifyContent: "center",
    alignItems: "center",
    borderWidth: 1,
    borderRadius: 35,
    height: "70%",
  },
  pressed: {
    opacity: 0.5,
  },
  signUpBtn: {
    backgroundColor: "black",
    alignItems: "center",
    justifyContent: "center",
    width: "60%",
    borderRadius: 25,
    height: 50,
  },
  centeredView: {
    flex: 1,
    marginTop: 22,
  },

  modelContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(230, 237, 232, 0.5)",
  },

  modelView: {
    backgroundColor: "#dfe0cc",
    justifyContent: "space-between",
    borderRadius: 15,
    padding: 30,
    gap: 4,
    width: "80%",
    height: "25%",
  },

  infoModelView: {
    backgroundColor: "#dfe0cc",
    justifyContent: "space-between",
    borderRadius: 15,
    padding: 50,
    gap: 4,
    width: "70%",
    height: "38%",
  },

  neededDocument: {
    fontSize: 15,
  },
});
