import pandas as pd
from sklearn.impute import SimpleImputer
from sklearn.model_selection import train_test_split


def load_data(filepath):
    """
    Reads a dataset from `filepath` attempting '|' then ',' separators.
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

    def preprocess(self):
        """
        Cleans data, removes IDs, and handles missing values.
        """
        drop_cols = ['Name', 'md5']
        cols_to_drop = [c for c in drop_cols if c in self.df.columns]
        data_clean = self.df.drop(columns=cols_to_drop)
        print(f"Dropped ID columns: {cols_to_drop}")

        target_col = 'legitimate'
        if target_col not in data_clean.columns:
            raise ValueError(f"Target column '{target_col}' not found in dataset!")

        X = data_clean.drop(columns=[target_col])
        y = data_clean[target_col]

        imputer = SimpleImputer(strategy='mean')
        X = pd.DataFrame(imputer.fit_transform(X), columns=X.columns)

        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )
        print(f"Training set: {self.X_train.shape}")
        print(f"Test set: {self.X_test.shape}")
