from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

model = None


def _get_model():
    global model
    if model is None:
        model = SentenceTransformer("all-MiniLM-L6-v2")
    return model


def job_match(cv_text, job_description):
    if not cv_text or not job_description:
        return None

    embeddings = _get_model().encode([cv_text, job_description])

    score = cosine_similarity(
        [embeddings[0]],
        [embeddings[1]]
    )[0][0]

    return float(round(score, 2))
