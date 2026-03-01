import pandas as pd
from sklearn.impute import SimpleImputer
from sklearn.model_selection import train_test_split
from sklearn.feature_selection import mutual_info_classif


def load_data(filepath):
    """Reads a dataset from `filepath` attempting '|' then ',' separators.
    Returns a pandas DataFrame.
    """
    try:
        df = pd.read_csv(filepath, sep='|')
        print("--- Data Loaded Successfully (sep='|') ---")
    except Exception:
        print("Warning: Failed to read with '|', trying ','...")
        df = pd.read_csv(filepath, sep=',')

    print(f"Shape: {df.shape}")
    print(f"Columns: {list(df.columns[:10])} ...")
    return df


class MalwareDataProcessor:
    def __init__(self, df):
        self.df = df
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.selected_features = None

    def preprocess(self, top_k=10, test_size=0.2, val_size=0.1, mi_sample_size=20000):
        """Clean data, impute missing values, select top_k features and split.

        Parameters:
        - top_k: int number of top features to keep (default 10)
        - test_size: fraction of data to hold out as final test set
        - val_size: fraction of data to use as validation (from the original dataset)
        Note: val_size is relative to the whole dataset. The function will first
        split off `test_size`, then split the remaining data to create a validation set
        of size `val_size` of the original dataset.
        """
        # 1. Remove Identifier Columns (Name, md5)
        drop_cols = ['Name', 'md5']

        cols_to_drop = [c for c in drop_cols if c in self.df.columns]
        data_clean = self.df.drop(columns=cols_to_drop)
        print(f"Dropped ID columns: {cols_to_drop}")

        # 2. Define Features (X) and Target (y)
        target_col = 'legitimate'
        if target_col not in data_clean.columns:
            raise ValueError(f"Target column '{target_col}' not found in dataset!")

        X = data_clean.drop(columns=[target_col])
        y = data_clean[target_col]

        # 3. Impute missing values
        imputer = SimpleImputer(strategy='mean')
        X = pd.DataFrame(imputer.fit_transform(X), columns=X.columns)

        # 4. Feature Selection using mutual information
        # For large datasets computing mutual information with nearest-neighbors
        # can be very slow. Compute MI on a random sample if dataset is large.
        if mi_sample_size is not None and X.shape[0] > int(mi_sample_size):
            samp = X.sample(n=int(mi_sample_size), random_state=42)
            y_samp = y.loc[samp.index]
            print(f"Computing mutual information on sample of {samp.shape[0]} rows (of {X.shape[0]})")
            mi_scores = mutual_info_classif(samp, y_samp, random_state=42)
        else:
            mi_scores = mutual_info_classif(X, y, random_state=42)
        # Ensure top_k is not larger than total features
        k = min(int(top_k), X.shape[1])
        top_indices = sorted(range(len(mi_scores)), key=lambda i: mi_scores[i], reverse=True)[:k]
        top_features = [X.columns[i] for i in top_indices]
        X_selected = X[top_features]
        self.selected_features = top_features

        print(f"Selected top {k} features: {top_features}")
        print(f"Features before: {X.shape[1]}, after: {X_selected.shape[1]}")

        # 5. Split Data into train / val / test
        # First split off the final test set
        X_remaining, X_test, y_remaining, y_test = train_test_split(
            X_selected, y, test_size=float(test_size), random_state=42, stratify=y
        )
        # Now compute validation fraction relative to the remaining set
        if val_size <= 0:
            X_train, X_val, y_train, y_val = X_remaining, X_remaining.iloc[0:0], y_remaining, y_remaining.iloc[0:0]
        else:
            rel_val_frac = float(val_size) / (1.0 - float(test_size))
            X_train, X_val, y_train, y_val = train_test_split(
                X_remaining, y_remaining, test_size=rel_val_frac, random_state=42, stratify=y_remaining
            )

        self.X_train, self.X_val, self.X_test = X_train, X_val, X_test
        self.y_train, self.y_val, self.y_test = y_train, y_val, y_test

        print(f"Training set: {self.X_train.shape}")
        print(f"Validation set: {self.X_val.shape}")
        print(f"Test set: {self.X_test.shape}")