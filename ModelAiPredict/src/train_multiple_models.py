import os
import json
import joblib
import numpy as np
from pprint import pprint

from data_preparation_virus import load_data, MalwareDataProcessor

from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import AdaBoostClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score, f1_score, roc_auc_score, classification_report, confusion_matrix

try:
    from xgboost import XGBClassifier
    HAS_XGBOOST = True
except Exception:
    HAS_XGBOOST = False

try:
    import tensorflow as tf
    from tensorflow.keras.models import Sequential
    from tensorflow.keras.layers import LSTM, GRU, Dense, Dropout
    from tensorflow.keras.callbacks import EarlyStopping
    HAS_TF = True
except Exception:
    HAS_TF = False


def prepare_data(df, top_k=12, scale_for_models=True):
    proc = MalwareDataProcessor(df)
    # create train/val/test splits inside the processor
    proc.preprocess(top_k=top_k, test_size=0.2, val_size=0.1)

    X_train = proc.X_train.copy()
    X_val = proc.X_val.copy()
    X_test = proc.X_test.copy()
    y_train = proc.y_train
    y_val = proc.y_val
    y_test = proc.y_test

    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_val_scaled = scaler.transform(X_val) if len(X_val) > 0 else np.empty((0, X_train.shape[1]))
    X_test_scaled = scaler.transform(X_test)

    return {
        'proc': proc,
        'X_train': X_train,
        'X_val': X_val,
        'X_test': X_test,
        'X_train_scaled': X_train_scaled,
        'X_val_scaled': X_val_scaled,
        'X_test_scaled': X_test_scaled,
        'y_train': y_train,
        'y_val': y_val,
        'y_test': y_test,
        'scaler': scaler,
    }


def train_xgboost(X, y):
    if not HAS_XGBOOST:
        print("XGBoost not available. Install with: pip install xgboost")
        return None
    model = XGBClassifier(use_label_encoder=False, eval_metric='logloss', n_jobs=-1, random_state=42)
    model.fit(X, y)
    return model


def train_adaboost(X, y):
    model = AdaBoostClassifier(n_estimators=100, random_state=42)
    model.fit(X, y)
    return model


def train_svm(X, y):
    model = SVC(kernel='rbf', probability=True, random_state=42)
    model.fit(X, y)
    return model


def build_rnn_model(input_shape, rnn_type='LSTM'):
    model = Sequential()
    if rnn_type == 'LSTM':
        model.add(LSTM(64, input_shape=input_shape))
    else:
        model.add(GRU(64, input_shape=input_shape))
    model.add(Dropout(0.3))
    model.add(Dense(32, activation='relu'))
    model.add(Dense(1, activation='sigmoid'))
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    return model


def train_lstm_gru(X_train, y_train, X_val, y_val, rnn_type='LSTM', epochs=30, batch_size=256):
    if not HAS_TF:
        print("TensorFlow not available. Install with: pip install tensorflow")
        return None
    # reshape: (samples, timesteps, features) -> treat each feature as timestep with 1 channel
    X_train_r = X_train.reshape((X_train.shape[0], X_train.shape[1], 1))
    X_val_r = X_val.reshape((X_val.shape[0], X_val.shape[1], 1))

    model = build_rnn_model(input_shape=(X_train_r.shape[1], 1), rnn_type=rnn_type)
    es = EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)
    model.fit(X_train_r, y_train, validation_data=(X_val_r, y_val), epochs=epochs, batch_size=batch_size, callbacks=[es], verbose=1)
    return model


def evaluate_model(name, model, X_test, y_test, is_nn=False):
    print(f"\n--- Evaluating {name} ---")
    if model is None:
        print("Model not available.")
        return
    if is_nn:
        X_in = X_test.reshape((X_test.shape[0], X_test.shape[1], 1))
        probs = model.predict(X_in).ravel()
        preds = (probs >= 0.5).astype(int)
    else:
        probs = model.predict_proba(X_test)[:, 1]
        preds = model.predict(X_test)

    acc = accuracy_score(y_test, preds)
    f1 = f1_score(y_test, preds)
    auc = roc_auc_score(y_test, probs)
    print(f"Accuracy: {acc:.4f}, F1: {f1:.4f}, AUC: {auc:.4f}")
    print("Confusion Matrix:")
    print(confusion_matrix(y_test, preds))
    print("Classification Report:")
    print(classification_report(y_test, preds, target_names=['Malware','Legitimate']))


def save_artifacts(output_dir, models_dict, scaler, feature_list):
    os.makedirs(output_dir, exist_ok=True)
    # save feature list
    with open(os.path.join(output_dir, 'feature_names.json'), 'w') as f:
        json.dump(feature_list, f)
    # save scaler
    joblib.dump(scaler, os.path.join(output_dir, 'scaler.joblib'))
    # save sklearn/xgboost models
    for name, mdl in models_dict.items():
        if mdl is None:
            continue
        if HAS_TF and hasattr(mdl, 'save') and isinstance(mdl, tf.keras.Model):
            # Keras model
            mdl.save(os.path.join(output_dir, f"{name}.keras"))
        else:
            joblib.dump(mdl, os.path.join(output_dir, f"{name}.joblib"))


def main(dataset_path='data/malware.csv', top_k=12, out_dir='models/multi'):
    if not os.path.exists(dataset_path):
        raise FileNotFoundError(dataset_path)

    df = load_data(dataset_path)
    data = prepare_data(df, top_k=top_k)

    X_train = data['X_train'].values
    X_test = data['X_test'].values
    X_train_scaled = data['X_train_scaled']
    X_test_scaled = data['X_test_scaled']
    y_train = data['y_train'].values
    y_test = data['y_test'].values

    models = {}

    # XGBoost (works on unscaled or scaled)
    if HAS_XGBOOST:
        print('\nTraining XGBoost...')
        models['xgboost'] = train_xgboost(X_train, y_train)
    else:
        print('\nSkipping XGBoost (not installed)')
        models['xgboost'] = None

    # RandomForest
    print('\nTraining RandomForest...')
    models['random_forest'] = RandomForestClassifier(n_estimators=100, max_depth=20, random_state=42, n_jobs=-1)
    models['random_forest'].fit(X_train, y_train)

    # AdaBoost (tree-based)
    print('\nTraining AdaBoost...')
    models['adaboost'] = train_adaboost(X_train, y_train)

    # SVM (use scaled data)
    print('\nTraining SVM (scaled)...')
    models['svm'] = train_svm(X_train_scaled, y_train)

    # LSTM & GRU (neural nets) - use scaled data
    if HAS_TF:
        print('\nTraining LSTM...')
        models['lstm'] = train_lstm_gru(X_train_scaled, y_train, data['X_val_scaled'], data['y_val'].values, rnn_type='LSTM')
        print('\nTraining GRU...')
        models['gru'] = train_lstm_gru(X_train_scaled, y_train, data['X_val_scaled'], data['y_val'].values, rnn_type='GRU')
    else:
        print('\nSkipping LSTM/GRU (TensorFlow not installed)')
        models['lstm'] = None
        models['gru'] = None

    # Evaluate
    if models.get('xgboost') is not None:
        evaluate_model('XGBoost', models['xgboost'], X_test, y_test)
    evaluate_model('RandomForest', models['random_forest'], X_test, y_test)
    evaluate_model('AdaBoost', models['adaboost'], X_test, y_test)
    evaluate_model('SVM', models['svm'], X_test_scaled, y_test)
    if models.get('lstm') is not None:
        evaluate_model('LSTM', models['lstm'], data['X_test_scaled'], data['y_test'].values, is_nn=True)
    if models.get('gru') is not None:
        evaluate_model('GRU', models['gru'], data['X_test_scaled'], data['y_test'].values, is_nn=True)

    save_artifacts(out_dir, models, data['scaler'], data['proc'].selected_features)
    print(f"Artifacts saved to {out_dir}")


if __name__ == '__main__':
    main()
