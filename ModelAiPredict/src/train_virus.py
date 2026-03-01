import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    accuracy_score,
    f1_score,
    roc_auc_score,
)
from data_preparation_virus import MalwareDataProcessor, load_data


class MalwareDetector:
    def __init__(self):
        # RandomForest is robust for tabular data
        self.model = RandomForestClassifier(
            n_estimators=100, max_depth=20, random_state=42, n_jobs=-1
        )

    def train(self, X_train, y_train):
        print("\n--- Training Random Forest Model ---")
        self.model.fit(X_train, y_train)
        print("Training Completed.")

    def evaluate(self, X_test, y_test):
        print("\n" + "=" * 30)
        print("MODEL EVALUATION REPORT")
        print("=" * 30)

        y_pred = self.model.predict(X_test)
        y_probs = self.model.predict_proba(X_test)[:, 1]

        acc = accuracy_score(y_test, y_pred)
        print(f"Accuracy: {acc:.4f}")

        f1 = f1_score(y_test, y_pred)
        print(f"F1-Score: {f1:.4f}")

        auc = roc_auc_score(y_test, y_probs)
        print(f"AUC-ROC:  {auc:.4f}")

        cm = confusion_matrix(y_test, y_pred)
        print("\nConfusion Matrix:")
        print(cm)
        print("(Row=Actual, Col=Predicted)")

        print("\nDetailed Report:")
        print(classification_report(y_test, y_pred, target_names=['Malware', 'Legitimate']))

        importances = self.model.feature_importances_
        indices = np.argsort(importances)[::-1]

        print("\nTop 5 Important Features:")
        for f in range(min(5, len(indices))):
            idx = indices[f]
            try:
                name = X_test.columns[idx]
            except Exception:
                name = f"feature_{idx}"
            print(f"{f+1}. {name} ({importances[idx]:.4f})")

        return y_test, y_probs


def export_model_to_onnx(model, feature_names, output_path, target_opset=13):
    """Export a scikit-learn model to ONNX format.
    Returns True on success, False otherwise.
    """
    try:
        from skl2onnx import convert_sklearn
        from skl2onnx.common.data_types import FloatTensorType
    except Exception as e:
        print("skl2onnx is not installed. Install it with: pip install skl2onnx onnx")
        print(f"Import error: {e}")
        return False

    initial_type = [("input", FloatTensorType([None, len(feature_names)]))]
    try:
        onnx_model = convert_sklearn(model, initial_types=initial_type, target_opset=target_opset)
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        with open(output_path, "wb") as f:
            f.write(onnx_model.SerializeToString())
        print(f"Model exported to ONNX: {output_path}")
        return True
    except Exception as e:
        print(f"Failed to convert to ONNX: {e}")
        return False


if __name__ == "__main__":
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    dataset_path = os.path.join(project_root, "data", "malware.csv")
    if not os.path.exists(dataset_path):
        print(f"Error: File '{dataset_path}' not found.")
        print("Please ensure the dataset is uploaded correctly.")
    else:
        df = load_data(dataset_path)
        processor = MalwareDataProcessor(df)
        # reduce feature count to ease downstream tooling 
        processor.preprocess(top_k=12)

        # Combine Train and Validation sets to use all available training data
        print(f"Merging Validation set ({len(processor.X_val)}) into Training set ({len(processor.X_train)})...")
        X_train_full = pd.concat([processor.X_train, processor.X_val])
        y_train_full = pd.concat([processor.y_train, processor.y_val])

        detector = MalwareDetector()
        detector.train(X_train_full, y_train_full)

        y_test, y_probs = detector.evaluate(processor.X_test, processor.y_test)

        # Export to ONNX
        out_path = os.path.join(project_root, "models", "malware_rf.onnx")
        feature_names = processor.selected_features or list(processor.X_train.columns)
        export_model_to_onnx(detector.model, feature_names, out_path)

    
