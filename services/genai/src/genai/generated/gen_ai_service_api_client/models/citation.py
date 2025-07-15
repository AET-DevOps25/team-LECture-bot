from collections.abc import Mapping
from typing import Any, TypeVar, Union, cast

from attrs import define as _attrs_define
from attrs import field as _attrs_field

from ..types import UNSET, Unset

T = TypeVar("T", bound="Citation")


@_attrs_define
class Citation:
    """
    Attributes:
        document_id (str): The ID of the document from which the citation was retrieved. Example: doc-123.
        chunk_id (str): A unique identifier for the specific chunk within the document (e.g., chunk index, page number).
            Example: 0.
        retrieved_text_preview (str): A short snippet of the text that was retrieved and used as context. Example:
            LangChain is a framework for developing applications powered by language models..
        document_name (Union[None, Unset, str]): The name or title of the source document. Example: Lecture Slides Week
            5.
    """

    document_id: str
    chunk_id: str
    retrieved_text_preview: str
    document_name: Union[None, Unset, str] = UNSET
    additional_properties: dict[str, Any] = _attrs_field(init=False, factory=dict)

    def to_dict(self) -> dict[str, Any]:
        document_id = self.document_id

        chunk_id = self.chunk_id

        retrieved_text_preview = self.retrieved_text_preview

        document_name: Union[None, Unset, str]
        if isinstance(self.document_name, Unset):
            document_name = UNSET
        else:
            document_name = self.document_name

        field_dict: dict[str, Any] = {}
        field_dict.update(self.additional_properties)
        field_dict.update(
            {
                "document_id": document_id,
                "chunk_id": chunk_id,
                "retrieved_text_preview": retrieved_text_preview,
            }
        )
        if document_name is not UNSET:
            field_dict["document_name"] = document_name

        return field_dict

    @classmethod
    def from_dict(cls: type[T], src_dict: Mapping[str, Any]) -> T:
        d = dict(src_dict)
        document_id = d.pop("document_id")

        chunk_id = d.pop("chunk_id")

        retrieved_text_preview = d.pop("retrieved_text_preview")

        def _parse_document_name(data: object) -> Union[None, Unset, str]:
            if data is None:
                return data
            if isinstance(data, Unset):
                return data
            return cast(Union[None, Unset, str], data)

        document_name = _parse_document_name(d.pop("document_name", UNSET))

        citation = cls(
            document_id=document_id,
            chunk_id=chunk_id,
            retrieved_text_preview=retrieved_text_preview,
            document_name=document_name,
        )

        citation.additional_properties = d
        return citation

    @property
    def additional_keys(self) -> list[str]:
        return list(self.additional_properties.keys())

    def __getitem__(self, key: str) -> Any:
        return self.additional_properties[key]

    def __setitem__(self, key: str, value: Any) -> None:
        self.additional_properties[key] = value

    def __delitem__(self, key: str) -> None:
        del self.additional_properties[key]

    def __contains__(self, key: str) -> bool:
        return key in self.additional_properties
