package cn.nkpro.easis.docengine;

public enum NkDocCycle{
        beforeCreate,
        afterCreated,

        beforeCalculate,
        afterCalculated,

        beforeUpdate,
        afterUpdated,
        afterUpdateCommitted,

        afterCopied,

        beforeDelete,
        afterDeleted,
        afterDeleteCommitted,
}